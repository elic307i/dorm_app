from django.urls import path
from .views import BuildingListView

urlpatterns = [
    path('buildings/', BuildingListView.as_view(), name='building-list'),
]

