from flaskapp.resources.auth import RegisterApi, LoginApi


# API endpoints
def initialize_routes(api):
    api.add_resource(RegisterApi, '/user/register')
    api.add_resource(LoginApi, '/user/login')
    # api.add_resource(LogoutApi, '/user/logout') # TODO
    # api.add_resource(PasswordChangeApi, '/user/password') # TODO
    # api.add_resource(UserDataApi, '/user/<username>') # TODO

    # api.add_resource(CaffDataApi, '/caff/<id>') # TODO
    # api.add_resource(CaffSearchApi, '/caff/search') # TODO
    # api.add_resource(CaffUploadApi, '/caff/upload') # TODO
    # api.add_resource(CaffDownloadApi, '/caff/download/<id>') # TODO
    #
    # api.add_resource(CommentApi, '/comment') # TODO
    # api.add_resource(DeleteCommentApi, '/comment/<id>') # TODO
