from flaskapp.resources.authentication import RegisterApi, LoginApi, LogoutApi, PasswordChangeApi
from flaskapp.resources.user import UsersListApi, UserDataApi
from flaskapp.resources.caff import CaffUploadApi, CaffDownloadApi, CaffDataApi, CaffSearchApi
from flaskapp.resources.comment import CommentApi



# API endpoints
def initialize_routes(api, limiter):
    RegisterApi.method_decorators.append(limiter.limit('10/hour', methods=['POST']))
    LoginApi.method_decorators.append(limiter.limit('30/minute', methods=['POST']))
    PasswordChangeApi.method_decorators.append(limiter.limit('5/day', methods=['POST']))
    CaffSearchApi.method_decorators.append(limiter.limit('1/second', methods=['GET']))
    CaffDownloadApi.method_decorators.append(limiter.limit('10/minute', methods=['GET']))
    CaffUploadApi.method_decorators.append(limiter.limit('10/minute', methods=['POST']))
    CommentApi.method_decorators.append(limiter.limit('10/minute', methods=['POST']))
    UserDataApi.method_decorators.append(limiter.limit('30/minute', methods=['DELETE']))
    CaffDataApi.method_decorators.append(limiter.limit('30/minute', methods=['DELETE']))

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
