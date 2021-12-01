RESPONSE_FORBIDDEN = {"errorId": "002", "errorMessage": "forbidden interaction"}
RESPONSE_INVALID_PAGE = {"errorId": "003", "errorMessage": "invalid page number"}
RESPONSE_INVALID_PER_PAGE = {"errorId": "004", "errorMessage": "invalid per page number"}

RESPONSE_USERNAME_EMPTY = {"errorId": "101", "errorMessage": "username cannot be empty"}
RESPONSE_USERNAME_TAKEN = {"errorId": "104", "errorMessage": {"username": "username is already taken"}}
RESPONSE_PASSWORD_EMPTY = {"errorId": "111", "errorMessage": "password cannot be empty"}
RESPONSE_PASSWORD_WEAK = {"errorId": "114", "errorMessage": "password too weak"}
RESPONSE_PASSWORD_SHORT = {"errorId": "120", "errorMessage": "password too short"}
RESPONSE_INVALID_USERNAME_OR_PASSWORD = {"errorId": "120", "errorMessage": "invalid username or password"}

RESPONSE_USER_NOT_FOUND = {"errorId": "199", "errorMessage": 'user not found'}

RESPONSE_INVALID_FILE_FORMAT = {"errorId": "200", "errorMessage": "invalid file format"}
RESPONSE_FILE_DOES_NOT_EXIST = {"errorId": "299", "errorMessage": "file does not exist"}
RESPONSE_FILE_NOT_IN_REQUEST = {"errorId": "299", "errorMessage": "file not in request"}
RESPONSE_INVALID_UPLOAD_PARAMETERS = {"errorId": "299", "errorMessage": "invalid parameters"}

RESPONSE_COMMENT_TOO_LONG = {"errorId": "301 ", "errorMessage": "comment too long"}
RESPONSE_COMMENT_NOT_FOUND = {"errorId": "399 ", "errorMessage": "comment not found"}
