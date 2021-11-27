from flask import request, jsonify
from flaskapp.database.models import User
from flask_restful import Resource
from flask_jwt_extended import jwt_required


class CaffDataApi(Resource):
    @jwt_required()
    def get(self, caff_id):
        pass

    @jwt_required()
    def delete(self, caff_id):
        pass


class CaffSearchApi(Resource):
    @jwt_required()
    def get(self):
        pass


class CaffUploadApi(Resource):
    @jwt_required()
    def post(self):
        pass


class CaffDownloadApi(Resource):
    @jwt_required()
    def get(self, caff_id):
        pass
