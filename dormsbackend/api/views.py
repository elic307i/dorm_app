from django.shortcuts import render
from django_filters.rest_framework import DjangoFilterBackend
from rest_framework.renderers import JSONRenderer

# Create your views here.
from rest_framework import generics
from .models import DormitoriesplusBuilding
from .models import DormitoriesplusUser
from .serializers import DormitoriesplusBuildingSerializer
from .serializers import DormitoriesplusUserSerializer
from django.http import JsonResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt

class BuildingListView(generics.ListAPIView):
    queryset = DormitoriesplusBuilding.objects.all()
    serializer_class = DormitoriesplusBuildingSerializer

class UserListView(generics.ListAPIView):
    queryset = DormitoriesplusUser.objects.all()
    serializer_class = DormitoriesplusUserSerializer
    filter_backends = [DjangoFilterBackend]
    filterset_fields = ['username', 'password']
    renderer_classes = [JSONRenderer]
