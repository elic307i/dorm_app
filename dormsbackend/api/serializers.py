# api/serializers.py
from rest_framework import serializers
from .models import DormitoriesplusBuilding

class DormitoriesplusBuildingSerializer(serializers.ModelSerializer):
    class Meta:
        model = DormitoriesplusBuilding
        fields = '__all__'

