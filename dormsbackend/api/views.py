from django.shortcuts import render
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework.renderers import JSONRenderer
from rest_framework.views import APIView
from rest_framework.response import Response
from django.contrib.auth import authenticate
from django.contrib.auth.hashers import check_password
from rest_framework import status
from .models import DormitoriesplusRequest
from .serializers import DormitoriesplusRequestSerializer
from django.utils import timezone
from .models import DormitoriesplusInventorytracking
from .serializers import InventoryItemSerializer
from rest_framework.generics import ListAPIView
from .models import DormitoriesplusRoomassignment
from django.utils.timezone import now
from django.db.models.functions import TruncDate
from rest_framework import generics
from .models import DormitoriesplusBuilding
from .models import DormitoriesplusUser
from .serializers import DormitoriesplusBuildingSerializer
from .serializers import DormitoriesplusUserSerializer
from django.http import JsonResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt
from .models import DormitoriesplusItem

class InventoryItemListAPIView(ListAPIView):
    queryset = DormitoriesplusInventorytracking.objects.all()
    serializer_class = InventoryItemSerializer

class BuildingListView(generics.ListAPIView):
    queryset = DormitoriesplusBuilding.objects.all()
    serializer_class = DormitoriesplusBuildingSerializer

class UserListView(generics.ListAPIView):
    queryset = DormitoriesplusUser.objects.all()
    serializer_class = DormitoriesplusUserSerializer
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['username', 'password']
    renderer_classes = [JSONRenderer]

class LoginAPIView(APIView):
    def post(self, request):
        username = request.data.get("username")
        password = request.data.get("password")

        try:
            user = DormitoriesplusUser.objects.get(username=username)
        except DormitoriesplusUser.DoesNotExist:
            return Response({"success": False, "error": "Invalid username"}, status=400)

        if check_password(password, user.password):
            room_assignment = DormitoriesplusRoomassignment.objects.filter(user=user).order_by('-assigned_at').first()            
            room_id = room_assignment.room.id if room_assignment and room_assignment.room else None

            return Response({
            "success": True,
            "user_id": user.id,
            "username": user.username,
            "room_id": room_id
            })
        else:
            return Response({"success": False, "error": "Invalid password"}, status=400)

class SubmitRequestAPIView(APIView):
    def post(self, request):
        data = request.data.copy()
        request_type = data.get("request_type")

        data['created_at'] = timezone.now()
        data['updated_at'] = timezone.now()
        data['status'] = data.get('status', 'pending')

        today = now().date()
        user_id = data.get('user')

        if request_type == 'equipment_rental':
            required_fields = ['user', 'item', 'return_date']

            already_submitted = DormitoriesplusRequest.objects.filter(
                user_id=user_id,
                request_type='equipment_rental'
            ).annotate(date_only=TruncDate('created_at')).filter(date_only=today).exists()

            if already_submitted:
                return Response(
                    {'success': False, 'error': 'You can only submit one equipment request per day.'},
                    status=status.HTTP_400_BAD_REQUEST
                )

        elif request_type == 'fault_report':
            required_fields = ['user', 'fault_description', 'fault_type', 'urgency', 'room']

            already_submitted = DormitoriesplusRequest.objects.filter(
                user_id=user_id,
                request_type='fault_report'
            ).annotate(date_only=TruncDate('created_at')).filter(date_only=today).exists()

            if already_submitted:
                return Response(
                    {'success': False, 'error': 'You can only submit one fault report per day.'},
                    status=status.HTTP_400_BAD_REQUEST
                )

        else:
            return Response(
                {'success': False, 'error': 'Invalid request_type'},
                status=status.HTTP_400_BAD_REQUEST
            )

        missing = [field for field in required_fields if not data.get(field)]
        if missing:
            return Response(
                {'success': False, 'error': f'Missing fields: {", ".join(missing)}'},
                status=status.HTTP_400_BAD_REQUEST
            )

        serializer = DormitoriesplusRequestSerializer(data=data)
        if serializer.is_valid():
            serializer.save()
            return Response(
                {'success': True, 'message': f'{request_type} submitted'}
            )

        return Response(
            {'success': False, 'errors': serializer.errors},
            status=status.HTTP_400_BAD_REQUEST
        )

class UserRequestsAPIView(APIView):
    def get(self, request, user_id):
        request_type = request.query_params.get("request_type")

        try:
            queryset = DormitoriesplusRequest.objects.filter(user_id=user_id)

            if request_type:
                queryset = queryset.filter(request_type=request_type)

            # Load inventory items only if relevant
            item_lookup = {}
            if not request_type or request_type == "equipment_rental":
                inventory_items = DormitoriesplusInventorytracking.objects.all()
                item_lookup = {item.id: item.item_name for item in inventory_items}

            data = []
            for req in queryset:
                item_name = None
                if req.request_type == "equipment_rental":
                    try:
                        item_obj = DormitoriesplusItem.objects.get(id=req.item_id)
                        inventory_id = item_obj.inventory_id
                        item_name = item_lookup.get(inventory_id)
                    except DormitoriesplusItem.DoesNotExist:
                        item_name = "Unknown item"

                data.append({
                    "request_type": req.request_type,
                    "item_name": item_name,
                    "status": getattr(req, "status", "unknown"),
                    "return_date": getattr(req, "return_date", None),
                    "fault_description": getattr(req, "fault_description", None),
                    "fault_type": getattr(req, "fault_type", None),
                    "urgency": getattr(req, "urgency", None),
                    "created_at": req.created_at.strftime("%Y-%m-%d") if req.created_at else None,
                })

            return Response(data, status=status.HTTP_200_OK)

        except Exception as e:
            return Response(
                {
                    "success": False,
                    "error": "An error occurred while processing your request.",
                    "details": str(e),
                },
                status=status.HTTP_500_INTERNAL_SERVER_ERROR
            )



class AvailableItemsAPIView(APIView):
    def get(self, request):
        inventory_id = request.query_params.get('inventory_id')

        if not inventory_id:
            return Response({'error': 'Missing inventory_id parameter'}, status=status.HTTP_400_BAD_REQUEST)

        try:
            items = DormitoriesplusItem.objects.filter(
                status='available',
                inventory_id=inventory_id
            )
        except ValueError:
            return Response({'error': 'Invalid inventory_id'}, status=status.HTTP_400_BAD_REQUEST)

        serialized_items = [
            {
                'id': item.id,  # ID of the actual item
                'inventory_id': item.inventory_id,
            }
            for item in items
        ]

        return Response(serialized_items, status=status.HTTP_200_OK)
