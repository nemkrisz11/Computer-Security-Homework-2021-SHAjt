from flask import request, jsonify, make_response, send_file, current_app
from mongoengine import DoesNotExist, ValidationError, Q
from PIL import Image
from flaskapp.database.models import CaffFile, CaffAnimationImage
from flaskapp.responses import *
from flask_restful import Resource
from flask_jwt_extended import jwt_required, current_user
from flask_jwt_extended import get_current_user
from caffparser import CiffFile, CiffFileHeader, CaffCredits, CaffHeader, CaffParser
from datetime import datetime, timedelta
from math import ceil
import os
import logging
import numpy as np

ALLOWED_EXTENSIONS = {'caff'}


# Checking for correct file format (CAFF)
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS


# Apply CAFF parser and extract features into JSON format
def create_preview_caff_file(file: CaffFile):
    caff_parser = CaffParser()

    with open(os.path.join(os.environ.get('UPLOAD_FOLDER'), str(file.id)), "rb") as f:
        numpy_data = np.fromfile(f, np.dtype('B'))

    parsed_file = caff_parser.parse(numpy_data)

    width = file.caffAnimationImage.width
    height = file.caffAnimationImage.height
    pixel_array = np.array(parsed_file.animationImages[0].ciffImage.pixelValues, dtype=np.uint8)
    pixel_array.resize(height, width, 3)

    preview = Image.fromarray(pixel_array)
    preview.thumbnail((256, 256))

    compressed_array = np.array(preview.convert('RGB'))

    preview_file = {
        "id": str(file.id),
        "numOfCiffs": file.numOfCiffs,
        "creationDate": int(datetime.timestamp(file.creationDate) * 1000),
        "creator": file.creator,
        "uploadDate": int(datetime.timestamp(file.uploadDate) * 1000),
        "uploaderName": file.uploaderName,
        "caffName": file.caffName,
        "caffAnimationImage": {
            "duration": file.caffAnimationImage.duration,
            "width": file.caffAnimationImage.width,
            "height": file.caffAnimationImage.height,
            "caption": file.caffAnimationImage.caption,
            "tags": file.caffAnimationImage.tags,
            "pixelValues": compressed_array.tolist()
        }
    }
    current_app.logger.setLevel(logging.INFO)
    current_app.logger.info('CAFF file was parsed with the following name: ' + str(file.caffName))
    return preview_file


def create_preview_caff_file_list(files):
    file_list = []
    for file in files:
        file_list.append(create_preview_caff_file(file))

    return file_list


# API for fetching or removing stored CAFF files based on file id
class CaffDataApi(Resource):
    @jwt_required()
    def get(self, caff_id):
        try:
            stored_file = CaffFile.objects.get(id=caff_id)
        except (DoesNotExist, ValidationError):
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('CAFF file does not exist with following id: ' + str(caff_id))
            return make_response(jsonify(RESPONSE_FILE_DOES_NOT_EXIST), 404)

        preview_file = create_preview_caff_file(stored_file)
        return make_response(jsonify(preview_file), 200)

    @jwt_required()
    def delete(self, caff_id):
        if current_user.isAdmin:
            try:
                stored_file = CaffFile.objects.get(id=caff_id)
            except (DoesNotExist, ValidationError):
                current_app.logger.setLevel(logging.ERROR)
                current_app.logger.error('CAFF file does not exist with following id: ' + str(caff_id))
                return make_response(jsonify(RESPONSE_FILE_DOES_NOT_EXIST), 404)

            filepath = os.path.join(os.environ.get('UPLOAD_FOLDER'), str(stored_file.id))

            if os.path.exists(filepath):
                os.remove(filepath)
            stored_file.delete()
            current_app.logger.setLevel(logging.INFO)
            current_app.logger.info('CAFF file was deleted with the following name: ' + str(stored_file.caffName))
            return make_response(jsonify(message='CaffFile delete successful'), 200)
        else:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Deleting CAFF file is forbidden for user: ' + str(current_user.name))
            return make_response(jsonify(RESPONSE_FORBIDDEN), 403)


# API for browsing in the available CAFF files based on some features
class CaffSearchApi(Resource):
    @jwt_required()
    def get(self):
        page = request.args.get('page', 1, type=int)
        perpage = request.args.get('perpage', 20, type=int)
        searchterm = request.args.get('searchTerm', "", type=str)
        creator = request.args.get('username', "", type=str)
        uploadername = request.args.get('uploaderName', "", type=str)
        creationdate = request.args.get('creationDate', -1, type=int)
        uploaddate = request.args.get('uploadDate', -1, type=int)

        if page < 1:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Unsuccessful searching with invalid page number: ' + str(page))
            return make_response(jsonify(RESPONSE_INVALID_PAGE), 400)
        if perpage < 1:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Unsuccessful searching with invalid per page number: ' + str(perpage))
            return make_response(jsonify(RESPONSE_INVALID_PER_PAGE), 400)

        query = None
        if searchterm:
            query = Q(caffName__contains=searchterm)
        if creator:
            if query:
                query &= Q(creator__contains=creator)
            else:
                query = Q(creator__contains=creator)
        if uploadername:
            if query:
                query &= Q(uploaderName__contains=uploadername)
            else:
                query = Q(uploaderName__contains=uploadername)
        if creationdate != -1:
            start = datetime.fromtimestamp(creationdate / 1000)
            end = start + timedelta(days=1)
            if query:
                query &= Q(creationDate__gte=start) & Q(creationDate__lt=end)
            else:
                query = Q(creationDate__gte=start) & Q(creationDate__lt=end)
        if uploaddate != -1:
            start = datetime.fromtimestamp(uploaddate / 1000)
            end = start + timedelta(days=1)
            if query:
                query &= Q(uploadDate__gte=start) & Q(uploadDate__lt=end)
            else:
                query = Q(uploadDate__gte=start) & Q(uploadDate__lt=end)

        caff_files = None
        if query:
            caff_files = CaffFile.objects(query)
        else:
            caff_files = CaffFile.objects()
        caff_files_total_count = len(caff_files)

        try:
            files = caff_files.paginate(page=page, per_page=perpage)
        except:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Unsuccessful searching with invalid page number: ' + str(page))
            return make_response(jsonify(RESPONSE_INVALID_PAGE), 400)

        if len(files.items) != 0:
            preview_file_list = create_preview_caff_file_list(files.items)
        else:
            preview_file_list = []

        return make_response(jsonify(caffs=preview_file_list, totalPages=ceil(caff_files_total_count / perpage)), 200)


def format_caff_name(name):
    formatted_name = name.strip()
    return formatted_name.replace("\"", "")


# API for uploading new CAFF file via POST method
class CaffUploadApi(Resource):
    @jwt_required()
    def post(self):
        if 'file' not in request.files:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Missing file from request')
            return make_response(jsonify(RESPONSE_FILE_NOT_IN_REQUEST), 400)

        name = request.form.get('name')
        file = request.files['file']
        if name is None or file is None:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Invalid parameters in request')
            return make_response(jsonify(RESPONSE_INVALID_UPLOAD_PARAMETERS), 400)

        if file.filename == '':
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Empty filename in request')
            return make_response(jsonify(RESPONSE_FILE_NOT_IN_REQUEST), 400)
        if not allowed_file(file.filename):
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Invalid file format in request')
            return make_response(jsonify(RESPONSE_INVALID_FILE_FORMAT), 400)

        uploader = get_current_user()

        caff_parser = CaffParser()
        file_bytes = file.stream.read()

        try:
            parsed_file = caff_parser.parse([b for b in file_bytes])
        except ValueError:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('Invalid file format cannot be parsed')
            return make_response(jsonify(RESPONSE_INVALID_FILE_FORMAT), 400)

        preview_animation_image = parsed_file.animationImages[0]

        name = format_caff_name(name)
        caff_file = CaffFile(
            caffName=name,
            numOfCiffs=parsed_file.header.numOfCiffs,
            creator=parsed_file.credits.creator,
            creationDate=datetime(
                parsed_file.credits.year,
                parsed_file.credits.month,
                parsed_file.credits.day,
                parsed_file.credits.hour,
                parsed_file.credits.minute
            ),
            comments=[],
            uploaderName=uploader.name,
            caffAnimationImage=CaffAnimationImage(
                duration=preview_animation_image.duration,
                width=preview_animation_image.ciffImage.header.width,
                height=preview_animation_image.ciffImage.header.height,
                caption=preview_animation_image.ciffImage.header.caption,
                tags=preview_animation_image.ciffImage.header.tags
            )
        )

        caff_file.save()

        filename = str(caff_file.id)

        # for docker
        with open(os.path.join(os.environ.get('UPLOAD_FOLDER'), filename), 'wb+') as f:
            f.write(file_bytes)

        current_app.logger.setLevel(logging.INFO)
        current_app.logger.info('New CAFF file was uploaded with the following name: ' + str(name))
        return make_response('', 201)


# Api for download CAFF file based on file id
class CaffDownloadApi(Resource):
    @jwt_required()
    def get(self, caff_id):
        try:
            stored_file = CaffFile.objects.get(id=caff_id)
        except (DoesNotExist, ValidationError):
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('CAFF file does not exist with following id: ' + str(caff_id))
            return make_response(jsonify(RESPONSE_FILE_DOES_NOT_EXIST), 404)

        filename = str(stored_file.id)
        filepath = os.path.join(os.environ.get('UPLOAD_FOLDER'), filename)
        if os.path.exists(filepath):
            current_app.logger.setLevel(logging.INFO)
            current_app.logger.info('CAFF file was downloaded with the following name: ' + str(stored_file.caffName))
            return send_file(path_or_file=filepath, as_attachment=True, download_name=stored_file.caffName)
        else:
            current_app.logger.setLevel(logging.ERROR)
            current_app.logger.error('CAFF file does not exist with following id: ' + str(caff_id))
            return make_response(jsonify(RESPONSE_FILE_DOES_NOT_EXIST), 404)
