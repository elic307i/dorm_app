# api/serializers.py
from rest_framework import serializers
from .models import DormitoriesplusBuilding
from .models import DormitoriesplusUser

class DormitoriesplusBuildingSerializer(serializers.ModelSerializer):
    class Meta:
        model = DormitoriesplusBuilding
        fields = '__all__'

class DormitoriesplusUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = DormitoriesplusUser
        fields = '__all__'
