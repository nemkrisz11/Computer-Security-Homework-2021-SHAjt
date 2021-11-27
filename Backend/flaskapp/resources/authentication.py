from flask import request, jsonify
from flaskapp.database.models import User
from flaskapp.authorization import jwt_redis_blocklist, TOKEN_EXPIRES
from flask_restful import Resource
from flask_jwt_extended import create_access_token, get_jwt, current_user
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
        return jsonify(id=str(id)), 200


class LoginApi(Resource):
    def post(self):
        body = request.get_json()
        user = User.objects.get(name=body.get('name'))
        authorized = user.check_password(body.get('password'))
        if not authorized:
            return jsonify(error="name or password invalid"), 401

        expires = datetime.timedelta(hours=1)
        access_token = create_access_token(identity=str(user.id), expires_delta=expires)
        return {'token': access_token}, 200


class LogoutApi(Resource):
    @jwt_required()
    def post(self):
        jti = get_jwt()["jti"]
        jwt_redis_blocklist.set(jti, "", ex=TOKEN_EXPIRES)
        return jsonify(msg="successful logout"), 200


class PasswordChangeApi(Resource):
    @jwt_required()
    def post(self):
        body = request.get_json()
        new_password = body.get('password')

        if current_user.admin:
            target_user = User.objects.get(name=body.get('name'))
            target_user.change_password(new_password)
            return jsonify(msg="password change successful"), 200
        else:
            if body.get('name') is not None:
                return jsonify(error="forbidden interaction"), 403
            else:
                current_user.change_password(new_password)
                return jsonify(msg="password change successful"), 200
