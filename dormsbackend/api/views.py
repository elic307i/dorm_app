from django.shortcuts import render

# Create your views here.
from rest_framework import generics
from .models import DormitoriesplusBuilding
from .serializers import DormitoriesplusBuildingSerializer
from django.http import JsonResponse
from django.db import connection
from django.views.decorators.csrf import csrf_exempt

class BuildingListView(generics.ListAPIView):
    queryset = DormitoriesplusBuilding.objects.all()
    serializer_class = DormitoriesplusBuildingSerializer
