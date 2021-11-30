from flaskapp.resources.authentication import RegisterApi, LoginApi, LogoutApi, PasswordChangeApi
from flaskapp.resources.user import UsersListApi, UserDataApi
from flaskapp.resources.caff import CaffUploadApi, CaffDownloadApi, CaffDataApi, CaffSearchApi
from flaskapp.resources.comment import CommentApi


# Defining Per-IP request-rate limits and register endpoints
def initialize_routes(api, limiter):
    RegisterApi.decorators = [limiter.limit('10/hour', methods=['POST'])]
    LoginApi.decorators = [limiter.limit('30/minute', methods=['POST'])]
    PasswordChangeApi.decorators = [limiter.limit('5/day', methods=['POST'])]
    CaffSearchApi.decorators = [limiter.limit('1/second', methods=['GET'])]
    CaffDownloadApi.decorators = [limiter.limit('10/minute', methods=['GET'])]
    CaffUploadApi.decorators = [limiter.limit('10/minute', methods=['POST'])]
    CommentApi.decorators = [limiter.limit('10/minute', methods=['POST'])]
    UserDataApi.decorators = [limiter.limit('30/minute', methods=['DELETE'])]
    CaffDataApi.decorators = [limiter.limit('30/minute', methods=['DELETE'])]

    api.add_resource(RegisterApi, '/user/register')
    api.add_resource(LoginApi, '/user/login')
    api.add_resource(LogoutApi, '/user/logout')
    api.add_resource(PasswordChangeApi, '/user/password')
    api.add_resource(UsersListApi, '/user/')
    api.add_resource(UserDataApi, '/user/<username>')

    api.add_resource(CaffDataApi, '/caff/<caff_id>')
    api.add_resource(CaffSearchApi, '/caff/search')
    api.add_resource(CaffUploadApi, '/caff/upload')
    api.add_resource(CaffDownloadApi, '/caff/download/<caff_id>')

    api.add_resource(CommentApi, '/comment')
