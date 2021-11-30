from flask import request, jsonify, make_response
from mongoengine import DoesNotExist
from flaskapp.database.models import User
from flask_restful import Resource
from flask_jwt_extended import jwt_required, current_user
from flaskapp.database.models import CaffFile, Comment

class CommentApi(Resource):
    @jwt_required()
    def get(self):
        body = request.get_json()
        caff_id = body.get('caffid')
        stored_file = None

        try:
            stored_file = CaffFile.objects.get(id=caff_id)
        except DoesNotExist:
            return make_response(jsonify(errorId="299", errorMessage="file does not exist"), 404)

        if stored_file.comments is []:
            return make_response(jsonify(errorId="399", errorMessage="comment not found"), 400)
        else:
            return make_response(jsonify(
                [{
                    "id": idx,
                    "caffId": stored_file.caff_id,
                    "username": c.username,
                    "comment": c,
                    "date": c.date
                } for idx, c in enumerate(stored_file.comments)]           
            ))

    @jwt_required()
    def post(self):
        body = request.get_json()
        if body is None:
            return make_response(jsonify(errorId="300", errorMessage="comment cannot be empty"), 400)

        caff_id = body.get('caffId')
        comment = body.get('comment')
        if comment is None:
            return make_response(jsonify(errorId="300", errorMessage="comment cannot be empty"), 400)
        if caff_id is None:
            return make_response(jsonify(errorId="300", errorMessage="comment cannot be empty"), 400)

        if len(comment) > 200:
            return make_response(jsonify(errorId="301 ", errorMessage="comment too long"), 400)

        try:
            stored_file = CaffFile.objects.get(id=caff_id)
        except DoesNotExist:
            return make_response(jsonify(errorId="299 ", errorMessage="caff does not exist"), 400)

        new_comment = Comment(
            username=current_user.name,
            comment=comment
        )

        stored_file.comments.append(new_comment)
        stored_file.save()
        return make_response(jsonify(message='comment created successful'), 200)


    @jwt_required()
    def delete(self):
        if current_user.isAdmin:
            caff_id = request.args.get('caffId', type=str)
            comment_id = request.args.get('commentId', type=int)
            if caff_id is None:
                return make_response(jsonify(errorId="399 ", errorMessage="caff id cannot be empty"), 400)
            if comment_id is None:
                return make_response(jsonify(errorId="399 ", errorMessage="comment id cannot be empty"), 400)


            try:
                stored_file = CaffFile.objects.get(id=caff_id)
                _ = stored_file.comments[comment_id]
                del stored_file.comments[comment_id]
                stored_file.save()
            except DoesNotExist:
                return make_response(jsonify(errorId="399 ", errorMessage="comment not found"), 400)
            except IndexError:
                return make_response(jsonify(errorId="399 ", errorMessage="comment not found"), 400)

            return make_response(jsonify(message='comment deleted successful'), 200)
        else:
            return make_response(jsonify(errorId="002", errorMessage='forbidden interaction'), 403)
