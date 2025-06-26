# api/serializers.py
from rest_framework import serializers
from .models import DormitoriesplusBuilding
from .models import DormitoriesplusUser
from .models import DormitoriesplusInventorytracking
from .models import DormitoriesplusRequest

class DormitoriesplusBuildingSerializer(serializers.ModelSerializer):
    class Meta:
        model = DormitoriesplusBuilding
        fields = '__all__'

class DormitoriesplusUserSerializer(serializers.ModelSerializer):
    class Meta:
        model = DormitoriesplusUser
        fields = '__all__'

class DormitoriesplusRequestSerializer(serializers.ModelSerializer):
    class Meta:
        model = DormitoriesplusRequest
        fields = '__all__'

class InventoryItemSerializer(serializers.ModelSerializer):
    class Meta:
        model = DormitoriesplusInventorytracking
        fields = ['id', 'item_name', 'item_name_en', 'item_name_he', 'quantity', 'photo_url']

