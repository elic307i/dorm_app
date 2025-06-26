# This is an auto-generated Django model module.
# You'll have to do the following manually to clean this up:
#   * Rearrange models' order
#   * Make sure each model has one field with primary_key=True
#   * Make sure each ForeignKey and OneToOneField has `on_delete` set to the desired behavior
#   * Remove `managed = False` lines if you wish to allow Django to create, modify, and delete the table
# Feel free to rename the models, but don't rename db_table values or field names.
from django.db import models

class DormitoriesplusBuilding(models.Model):
    id = models.BigAutoField(primary_key=True)
    building_name = models.CharField(max_length=255)
    created_at = models.DateTimeField()
    building_staff_member = models.ForeignKey('DormitoriesplusUser', models.DO_NOTHING, blank=True, null=True)
    building_name_en = models.CharField(max_length=255, blank=True, null=True)
    building_name_he = models.CharField(max_length=255, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'DormitoriesPlus_building'


class DormitoriesplusInventorytracking(models.Model):
    id = models.BigAutoField(primary_key=True)
    item_name = models.CharField(max_length=255)
    quantity = models.IntegerField()
    created_at = models.DateTimeField()
    updated_at = models.DateTimeField()
    photo_url = models.CharField(max_length=100, blank=True, null=True)
    item_name_en = models.CharField(max_length=255, blank=True, null=True)
    item_name_he = models.CharField(max_length=255, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'DormitoriesPlus_inventorytracking'


class DormitoriesplusItem(models.Model):
    id = models.BigAutoField(primary_key=True)
    status = models.CharField(max_length=20)
    created_at = models.DateTimeField()
    updated_at = models.DateTimeField()
    inventory = models.ForeignKey(DormitoriesplusInventorytracking, models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'DormitoriesPlus_item'


class DormitoriesplusMessage(models.Model):
    id = models.BigAutoField(primary_key=True)
    content = models.TextField()
    created_at = models.DateTimeField()
    building = models.ForeignKey(DormitoriesplusBuilding, models.DO_NOTHING, blank=True, null=True)
    content_en = models.TextField(blank=True, null=True)
    content_he = models.TextField(blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'DormitoriesPlus_message'


class DormitoriesplusRequest(models.Model):
    id = models.BigAutoField(primary_key=True)
    request_type = models.CharField(max_length=20)
    fault_description = models.TextField(blank=True, null=True)
    status = models.CharField(max_length=20)
    created_at = models.DateTimeField()
    updated_at = models.DateTimeField()
    item = models.ForeignKey(DormitoriesplusItem, models.DO_NOTHING, blank=True, null=True)
    user = models.ForeignKey('DormitoriesplusUser', models.DO_NOTHING)
    room = models.ForeignKey('DormitoriesplusRoom', models.DO_NOTHING, blank=True, null=True)
    note = models.TextField(blank=True, null=True)
    admin_note = models.TextField(blank=True, null=True)
    return_date = models.DateField(blank=True, null=True)
    fault_type = models.CharField(max_length=50, blank=True, null=True)
    urgency = models.CharField(max_length=20, blank=True, null=True)
    admin_note_en = models.TextField(blank=True, null=True)
    admin_note_he = models.TextField(blank=True, null=True)
    fault_description_en = models.TextField(blank=True, null=True)
    fault_description_he = models.TextField(blank=True, null=True)
    fault_type_en = models.CharField(max_length=50, blank=True, null=True)
    fault_type_he = models.CharField(max_length=50, blank=True, null=True)
    note_en = models.TextField(blank=True, null=True)
    note_he = models.TextField(blank=True, null=True)
    urgency_en = models.CharField(max_length=20, blank=True, null=True)
    urgency_he = models.CharField(max_length=20, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'DormitoriesPlus_request'


class DormitoriesplusRoom(models.Model):
    id = models.BigAutoField(primary_key=True)
    room_number = models.CharField(max_length=20)
    capacity = models.IntegerField()
    notes = models.TextField(blank=True, null=True)
    created_at = models.DateTimeField()
    building = models.ForeignKey(DormitoriesplusBuilding, models.DO_NOTHING)
    notes_en = models.TextField(blank=True, null=True)
    notes_he = models.TextField(blank=True, null=True)
    room_number_en = models.CharField(max_length=20, blank=True, null=True)
    room_number_he = models.CharField(max_length=20, blank=True, null=True)

    class Meta:
        managed = False
        db_table = 'DormitoriesPlus_room'
        unique_together = (('building', 'room_number'),)


class DormitoriesplusRoomassignment(models.Model):
    id = models.BigAutoField(primary_key=True)
    room = models.ForeignKey(DormitoriesplusRoom, models.DO_NOTHING)
    assigned_at = models.DateTimeField()
    user = models.ForeignKey('DormitoriesplusUser', models.DO_NOTHING)
    end_date = models.DateField(blank=True, null=True)
    start_date = models.DateField()

    class Meta:
        managed = False
        db_table = 'DormitoriesPlus_roomassignment'


class DormitoriesplusUser(models.Model):
    id = models.BigAutoField(primary_key=True)
    password = models.CharField(max_length=128)
    last_login = models.DateTimeField(blank=True, null=True)
    is_superuser = models.BooleanField()
    username = models.CharField(unique=True, max_length=150)
    first_name = models.CharField(max_length=150)
    last_name = models.CharField(max_length=150)
    is_staff = models.BooleanField()
    is_active = models.BooleanField()
    date_joined = models.DateTimeField()
    role = models.CharField(max_length=20)
    email = models.CharField(unique=True, max_length=254)
    field_encrypted_id_number = models.CharField(db_column='_encrypted_id_number', max_length=255, blank=True, null=True)  # Field renamed because it started with '_'.
    field_encrypted_phone = models.CharField(db_column='_encrypted_phone', max_length=255, blank=True, null=True)  # Field renamed because it started with '_'.
    
    class Meta:
        managed = False
        db_table = 'DormitoriesPlus_user'


class DormitoriesplusUserGroups(models.Model):
    user = models.ForeignKey(DormitoriesplusUser, models.DO_NOTHING)
    group = models.ForeignKey('AuthGroup', models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'DormitoriesPlus_user_groups'
        unique_together = (('user', 'group'),)


class DormitoriesplusUserUserPermissions(models.Model):
    user = models.ForeignKey(DormitoriesplusUser, models.DO_NOTHING)
    permission = models.ForeignKey('AuthPermission', models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'DormitoriesPlus_user_user_permissions'
        unique_together = (('user', 'permission'),)


class AuthGroup(models.Model):
    name = models.CharField(unique=True, max_length=150)

    class Meta:
        managed = False
        db_table = 'auth_group'


class AuthGroupPermissions(models.Model):
    group = models.ForeignKey(AuthGroup, models.DO_NOTHING)
    permission = models.ForeignKey('AuthPermission', models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'auth_group_permissions'
        unique_together = (('group', 'permission'),)


class AuthPermission(models.Model):
    name = models.CharField(max_length=255)
    content_type = models.ForeignKey('DjangoContentType', models.DO_NOTHING)
    codename = models.CharField(max_length=100)

    class Meta:
        managed = False
        db_table = 'auth_permission'
        unique_together = (('content_type', 'codename'),)


class DjangoAdminLog(models.Model):
    action_time = models.DateTimeField()
    object_id = models.TextField(blank=True, null=True)
    object_repr = models.CharField(max_length=200)
    action_flag = models.SmallIntegerField()
    change_message = models.TextField()
    content_type = models.ForeignKey('DjangoContentType', models.DO_NOTHING, blank=True, null=True)
    user = models.ForeignKey(DormitoriesplusUser, models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'django_admin_log'


class DjangoContentType(models.Model):
    app_label = models.CharField(max_length=100)
    model = models.CharField(max_length=100)

    class Meta:
        managed = False
        db_table = 'django_content_type'
        unique_together = (('app_label', 'model'),)


class DjangoMigrations(models.Model):
    app = models.CharField(max_length=255)
    name = models.CharField(max_length=255)
    applied = models.DateTimeField()

    class Meta:
        managed = False
        db_table = 'django_migrations'


class DjangoSession(models.Model):
    session_key = models.CharField(primary_key=True, max_length=40)
    session_data = models.TextField()
    expire_date = models.DateTimeField()

    class Meta:
        managed = False
        db_table = 'django_session'


class TokenBlacklistBlacklistedtoken(models.Model):
    id = models.BigAutoField(primary_key=True)
    blacklisted_at = models.DateTimeField()
    token = models.OneToOneField('TokenBlacklistOutstandingtoken', models.DO_NOTHING)

    class Meta:
        managed = False
        db_table = 'token_blacklist_blacklistedtoken'


class TokenBlacklistOutstandingtoken(models.Model):
    id = models.BigAutoField(primary_key=True)
    token = models.TextField()
    created_at = models.DateTimeField(blank=True, null=True)
    expires_at = models.DateTimeField()
    user = models.ForeignKey(DormitoriesplusUser, models.DO_NOTHING, blank=True, null=True)
    jti = models.CharField(unique=True, max_length=255)

    class Meta:
        managed = False
        db_table = 'token_blacklist_outstandingtoken'
