from django.shortcuts import render

# Create your views here.
from rest_framework import generics
from .models import DormitoriesplusBuilding
from .serializers import DormitoriesplusBuildingSerializer

class BuildingListView(generics.ListAPIView):
    queryset = DormitoriesplusBuilding.objects.all()
    serializer_class = DormitoriesplusBuildingSerializer

