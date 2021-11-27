from flaskapp.resources.authentication import RegisterApi, LoginApi, LogoutApi, PasswordChangeApi
from flaskapp.resources.user import UsersListApi


# API endpoints
def initialize_routes(api):
    api.add_resource(RegisterApi, '/user/register')
    api.add_resource(LoginApi, '/user/login')
    api.add_resource(LogoutApi, '/user/logout')
    api.add_resource(PasswordChangeApi, '/user/password')
    api.add_resource(UsersListApi, '/user/')
    # api.add_resource(UserDataApi, '/user/<username>') # TODO

    # api.add_resource(CaffDataApi, '/caff/<id>') # TODO
    # api.add_resource(CaffSearchApi, '/caff/search') # TODO
    # api.add_resource(CaffUploadApi, '/caff/upload') # TODO
    # api.add_resource(CaffDownloadApi, '/caff/download/<id>') # TODO
    #
    # api.add_resource(CommentApi, '/comment') # TODO
    # api.add_resource(DeleteCommentApi, '/comment/<id>') # TODO
