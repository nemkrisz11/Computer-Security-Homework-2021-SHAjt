from flask import request, jsonify
from flaskapp.database.models import User
from flask_restful import Resource
from flask_jwt_extended import jwt_required, current_user
from math import ceil


class UsersListApi(Resource):
    @jwt_required()
    def get(self):
        page = int(request.args.get('page'))
        perpage = int(request.args.get('perpage'))

        if page is None or perpage is None:
            return jsonify(errorMessage='incorrect arguments given'), 400

        users = User.objects.skip((page - 1) * perpage).limit(perpage)

        return jsonify(
            users=[{"username": u.name, "isAdmin": u.isAdmin, "regDate": u.regDate} for u in users],
            totalPages=ceil(User.objects.count() / perpage)
        ), 200


class UserDataApi(Resource):
    @jwt_required()
    def get(self, username):
        user = User.objects.get(name=username)

        if user is None:
            return jsonify(errorMessage='user not found'), 404

        return jsonify(username=user.name, idAdmin=user.isAdmin, regDate=user.regDate), 200

    @jwt_required()
    def delete(self, username):
        if current_user.isAdmin:
            user = User.objects.get(name=username)
            user.delete()
            return jsonify(message='user delete successful'), 200
        else:
            return jsonify(errorMessage='forbidden interaction'), 403
