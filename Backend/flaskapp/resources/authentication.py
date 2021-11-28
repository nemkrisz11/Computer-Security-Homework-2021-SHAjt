from flask import request, jsonify, make_response
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
        user = User(name=body.get('name'), password=body.get('password'))
        user.hash_password()
        user.save()
        id = user.id
        return make_response(jsonify(id=str(id)), 200)


class LoginApi(Resource):
    def post(self):
        body = request.get_json()
        user = User.objects.get(name=body.get('name'))
        authorized = user.check_password(body.get('password'))
        if not authorized:
            return make_response(jsonify(errorMessage="name or password invalid"), 401)

        access_token = create_access_token(identity=user, expires_delta=TOKEN_EXPIRES)
        return make_response(jsonify(token=access_token), 200)


class LogoutApi(Resource):
    @jwt_required()
    def post(self):
        jti = get_jwt()["jti"]
        jwt_redis_blocklist.set(jti, "", ex=TOKEN_EXPIRES)
        return make_response(jsonify(msg="successful logout"), 200)


class PasswordChangeApi(Resource):
    @jwt_required()
    def post(self):
        body = request.get_json()
        new_password = body.get('password')

        if current_user.isAdmin:
            target_user = User.objects.get(name=body.get('name'))
            target_user.change_password(new_password)
            return make_response(jsonify(message="password change successful"), 200)
        else:
            if body.get('name') is not None:
                return make_response(jsonify(errorMessage="forbidden interaction"), 403)
            else:
                current_user.change_password(new_password)
                return make_response(jsonify(message="password change successful"), 200)
