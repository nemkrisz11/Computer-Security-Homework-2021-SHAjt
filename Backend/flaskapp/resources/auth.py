from flask import request
from database.models import User
from flask_restful import Resource
from flask_jwt_extended import create_access_token
from flask_jwt_extended import jwt_required
import datetime

class RegisterApi(Resource):
    @jwt_required
    def post(self):
        body = request.get_json()
        user = User(**body)
        user.hash_password()
        user.save()
        id = user.id
        return {'id': str(id)}, 200

class LoginApi(Resource):
    def post(self):
        body = request.get_json()
        user = User.objects.get(name=body.get('name'))
        authorized = user.check_password(body.get('password'))
        if not authorized:
            return {'error': 'name or password invalid'}, 401

        expires = datetime.timedelta(hours=1)
        access_token = create_access_token(identity=str(user.id), expires_delta=expires)
        return {'token': access_token}, 200