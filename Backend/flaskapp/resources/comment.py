from math import ceil

from flask import request, jsonify, make_response
from mongoengine import DoesNotExist
from flaskapp.database.models import User
from flask_restful import Resource
from flask_jwt_extended import jwt_required, current_user
from flaskapp.database.models import CaffFile, Comment
from datetime import datetime
from flask import current_app
import logging

# API for fetching, removing and uploading comments


class CommentApi(Resource):
    # Fetching comments for CAFF file based on file id
    @jwt_required()
    def get(self):
        caff_id = request.args.get('caffId', type=str)
        page = request.args.get('page', 1, type=int)
        perpage = request.args.get('perpage', 20, type=int)

        try:
            stored_file = CaffFile.objects.get(id=caff_id)
        except DoesNotExist:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('CAFF file with id does not found: ' + str(caff_id))
            return make_response(jsonify(errorId="299", errorMessage="file does not exist"), 404)

        if not stored_file.comments:
            current_app.logger.setLevel(logging.INFO)
            current_app.logger.info('Empty comment list returned for CAFF id: ' + str(caff_id))
            return make_response(jsonify(comments=[], totalPages=0), 200)
        else:
            total_comment_count = len(stored_file.comments)
            comment_list = [{
                "id": idx,
                "caffId": str(stored_file.id),
                "username": c.username,
                "comment": c.comment,
                "date": int(datetime.timestamp(c.date) * 1000)
            } for idx, c in enumerate(stored_file.comments)]

            # given page doesn't have any elements
            if total_comment_count <= (page-1)*perpage:
                paginated_comment_list = []

            # given page is the last page
            if (page - 1)*perpage < total_comment_count <= page*perpage:
                if total_comment_count == 1:
                    paginated_comment_list = comment_list
                else:
                    paginated_comment_list = comment_list[(page - 1) * perpage:]

            # given page not the last page
            if total_comment_count > page*perpage:
                paginated_comment_list = comment_list[(page - 1) * perpage: (page * perpage)]

            return make_response(jsonify(comments=paginated_comment_list, totalPages=ceil(total_comment_count / perpage)), 200)

    # Uploading new comments with restrictions regarding length (between 0 and 200 chars)
    @jwt_required()
    def post(self):
        body = request.get_json()
        if body is None:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Body in request is empty')
            return make_response(jsonify(errorId="300", errorMessage="comment cannot be empty"), 400)

        caff_id = body.get('caffId')
        comment = body.get('comment')
        if comment is None:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Comment in request is empty')
            return make_response(jsonify(errorId="300", errorMessage="comment cannot be empty"), 400)
        if caff_id is None:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('CAFF id for comment in request is empty')
            return make_response(jsonify(errorId="300", errorMessage="comment cannot be empty"), 400)

        if len(comment) > 200:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Comment in request is too long')
            return make_response(jsonify(errorId="301 ", errorMessage="comment too long"), 400)

        try:
            stored_file = CaffFile.objects.get(id=caff_id)
        except DoesNotExist:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('CAFF file does not exist with id: ' + str(caff_id))
            return make_response(jsonify(errorId="299 ", errorMessage="caff does not exist"), 400)

        new_comment = Comment(
            username=current_user.name,
            comment=comment
        )

        stored_file.comments.append(new_comment)
        stored_file.save()
        current_app.logger.setLevel(logging.INFO)
        current_app.logger.info('Comment was successfully posted with the following username: ' + str(current_user.name) + ' for CAFF file with id: ' + str(caff_id))
        return make_response(jsonify(message='comment created successful'), 200)

    # Only admin can remove comments belonging to a CAFF file with file and comment id
    @jwt_required()
    def delete(self):
        if current_user.isAdmin:
            caff_id = request.args.get('caffId', type=str)
            comment_id = request.args.get('commentId', type=int)
            if caff_id is None:
                current_app.logger.setLevel(logging.ERROR)
                current_app.logger.error('CAFF id is empty')
                return make_response(jsonify(errorId="399 ", errorMessage="caff id cannot be empty"), 400)
            if comment_id is None:
                current_app.logger.setLevel(logging.ERROR)
                current_app.logger.error('Comment id is empty')
                return make_response(jsonify(errorId="399 ", errorMessage="comment id cannot be empty"), 400)


            try:
                stored_file = CaffFile.objects.get(id=caff_id)
                _ = stored_file.comments[comment_id]
                del stored_file.comments[comment_id]
                stored_file.save()
            except DoesNotExist:
                current_app.logger.setLevel(logging.ERROR)
                current_app.logger.error('Comment with id does not exist: ' + str(comment_id))
                return make_response(jsonify(errorId="399 ", errorMessage="comment not found"), 400)
            except IndexError:
                current_app.logger.setLevel(logging.ERROR)
                current_app.logger.error('Comment with id does not exist: ' + str(comment_id))
                return make_response(jsonify(errorId="399 ", errorMessage="comment not found"), 400)
            current_app.logger.setLevel(logging.INFO)
            current_app.logger.info('Comment with id: '+ str(comment_id)+' was successfully removed by the following user: ' + str(current_user.name) + ' for CAFF file with id: ' + str(caff_id))
            return make_response(jsonify(message='comment deleted successful'), 200)
        else:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('User not allowed to delete comment: ' + str(current_user.username))
            return make_response(jsonify(errorId="002", errorMessage='forbidden interaction'), 403)
