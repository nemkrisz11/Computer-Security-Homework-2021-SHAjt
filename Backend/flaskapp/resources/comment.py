from flask import request, jsonify
from flaskapp.database.models import User
from flask_restful import Resource
from flask_jwt_extended import jwt_required


class CommentApi(Resource):
    @jwt_required()
    def get(self):
        pass

    @jwt_required()
    def post(self):
        pass


class DeleteCommentApi(Resource):
    @jwt_required()
    def delete(self, comment_id):
        pass
