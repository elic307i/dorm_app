from django.urls import path
from .views import BuildingListView
from .views import UserListView
from .views import LoginAPIView
from .views import SubmitRequestAPIView
from .views import InventoryItemListAPIView
from .views import UserRequestsAPIView
from .views import AvailableItemsAPIView

urlpatterns = [
    	path('inventory-items/', InventoryItemListAPIView.as_view(), name='inventory-items'),
	path('available-items/', AvailableItemsAPIView.as_view(), name='available-items'),
	path('users/', UserListView.as_view(), name='user-list'),
	path('user-requests/<int:user_id>/', UserRequestsAPIView.as_view(), name='user-request'),
	path('submit-request/', SubmitRequestAPIView.as_view(), name='submit-request'),
	path('login/', LoginAPIView.as_view(), name='login'),
	path('buildings/', BuildingListView.as_view(), name='building-list'),
]
