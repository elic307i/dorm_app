from django.urls import path
from .views import BuildingListView
from .views import UserListView

urlpatterns = [
    	path('users/', UserListView.as_view(), name='user-list'),
	path('buildings/', BuildingListView.as_view(), name='building-list'),
]
