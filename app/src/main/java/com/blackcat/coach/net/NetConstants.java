package com.blackcat.coach.net;


import com.blackcat.coach.utils.Constants;

public class NetConstants {

	public static final String HTTP = "http";
    public static final String HTTPS = "https";
	public static final String ENCODING = "UTF-8";
	
	public static final boolean BASE64_ENCODING = true;
	/**测试*/
//	public static final String HOSTNAME = "101.200.204.240";
//	public static final String HOSTNAME = "123.57.63.15";、、123.56.185.59
	/***正式*/
	public static final String HOSTNAME = "jzapi.yibuxueche.com";

//	?coachid=5666365ef14c20d07ffa6ae8

//	public static final String HOSTNAME_TEST = "101.200.204.240:8183/api/v1/test";


	/***/
	public static final int DEFAULT_PORT = 80;

//	public static final int DEFAULT_PORT = 8183;


	//8183

	public static final String VERSION = "api/v1/";

	/****************************** 1.1 开始V2 ****************************************/
	//确认订单 列表
	public static final String CONFIRM_ORDER_LIST = "api/v2/courseinfo/getuconfirmcourse";


	/*******************************1.0 开始 V1**************************************************/
	//七牛相关
    public static final String PATH_QINIU_TOKEN = "api/v1/info/qiniuuptoken";


	// 个人中心
    public static final String PATH_LOGIN = "api/v1/userinfo/userlogin";
    public static final String PATH_REGISTER = "api/v1/userinfo/signup";
	public static final String PATH_UPDATE_PWD = "api/v1/userinfo/updatepwd";
	public static final String PATH_FEEDBACK = "api/v1/userfeedback";
	public static final String PATH_GET_MSG_CODE = "api/v1/code/";
	public static final String PATH_GET_COACHINFO = "api/v1/userinfo/getcoachinfo";
	public static final String PATH_PERSONAL_SETTING = "api/v1/userinfo/personalsetting";
	//消息（新加）
	public static final String PATH_GET_MESSAGE = "api/v1/userinfo/getmessagecount";

	public static final String PATH_UPDATE_COACHINFO = "api/v1/userinfo/updatecoachinfo";
	public static final String PATH_APPLY_VERIFY = "api/v1/userinfo/applyverification";
	public static final String PATH_UPDATE_MOBILE = "api/v1/userinfo/updatemobile";
	public static final String PATH_HANDLE_CLASS = "api/v1/courseinfo/coachhandleinfo";
	public static final String PATH_FINISH_CLASS = "api/v1/courseinfo/coachfinishreservation";
	public static final String PATH_RESERVATION_LIST = "api/v1/courseinfo/coachreservationlist";
	public static final String PATH_SCHEDULE_LIST = "api/v1/courseinfo/daysreservationlist";
	public static final String PATH_GET_STUDENTS = "api/v1/userinfo/coachstudentlist";
	public static final String PATH_GET_RESERVATIONINFO = "api/v1/courseinfo/reservationinfo/";
//	public static final String PATH_COACH_COMMENT = "api/v1/courseinfo/coachcomment";
	public static final String PATH_GET_TRAININGFIELD = "api/v1/getschooltrainingfield";
	public static final String PATH_GET_SUBJECTS = "api/v1/info/subject";
	public static final String PATH_GET_COMMENTS = "api/v1/courseinfo/getusercomment/1/";
	public static final String PATH_GET_STUDENTINFO = "api/v1/userinfo/studentinfo";
	public static final String PATH_GET_NEARBYSCHOOL = "api/v1/driveschool/nearbydriveschool";
	public static final String PATH_GET_SCHOOLBYNAME = "api/v1/getschoolbyname";
	public static final String PATH_GET_CLASSTYPE = "api/v1/userinfo/getcoachclasstype";
	public static final String PATH_SAVE_CLASSES = "api/v1/userinfo/coachsetclass";
	public static final String PATH_SET_WORKTIME = "api/v1/userinfo/coachsetworktime";
	public static final String PATH_SET_VACATION = "api/v1/courseinfo/putcoachleave";
	public static final String PATH_QINIU_UPTOKEN = PATH_QINIU_TOKEN;
	public static final String PATH_UPDATE_FEEDBACK = "api/v1/userfeedback";
	public static final String PATH_GET_MY_WALLET = "api/v1/userinfo/getmywallet";
	public static final String PATH_GET_MALL_PRODUCT = "api/v1/getmailproduct";
	public static final String PATH_GET_DETAIL_PRODUCT = "api/v1/getproductdetail";
	public static final String PATH_BUY_PRODUCT = "api/v1/userinfo/buyproduct";
	//添加
	public static final String PATH_SYSTEMMSG = "api/v1/userinfo/getnews";
	public static final String PATH_OREDERMSG = "api/v1/userinfo/getsysteminfo";
	/***教练评论*/
	public static final String PATH_GET_COMMENTS_CAOCH = "api/v1/courseinfo/getusercomment/2/";

	/***某一教练的预约情况*/
	public static final String GET_COURSE_BY_COACH = "api/v1/courseinfo/getcoursebycoach";
	/*** 教练给学员预约*/
	public static final String COACH_APPOINT_CAR = "api/v1/courseinfo/userreservationcourse";

	public static final String PATH_GET_GETMONTHAPPLYDATA = "api/v1/courseinfo/getmonthapplydata";


	//public static final String PATH_COACH_COMMENT = "api/v1/courseinfo/coachcomment";
	public static final String PATH_COACH_COMMENT = "api/v1/courseinfo/coachcommentv2";
	/**添加标签*/
	public static final String PATH_COACH_TAG_ADD= "api/v1/userinfo/coachaddtag";

	/**删除标签*/
	public static final String PATH_COACH_TAG_DELETE= "api/v1/userinfo/coachdeletetag";
	/**所有标签*/
	public static final String PATH_COACH_TAGS= "api/v1/userinfo/getallcoachtags";

	public static final String PATH_COACH_SET_TAG = "api/v1/userinfo/coachsetselftags";

	/***教练获取某一节课的课时详情*/
	public static final String PATH_COURSE_DETAIL = "api/v1/courseinfo/coursereservationlist";
//	get /

	//新加（签到）
	public static final String PATH_SIGNIN = "api/v1/courseinfo/coursesignin";
	/***提醒学员可以报考了*/
	public static final String PATH_REMINDEXAM = "api/v1/userinfo/remindexam";
	public static final String PATH_SEARCH = "api/v1/courseinfo/searchreservationlist";
	/*************************************************/






	/***教练日程数据按时段划分*/
	public static final String PATH_GETDAYTIMERESERVATION = "/api/v2/courseinfo/daytimelysreservation";



	public static final String PATH_UPLOAD_AVATAR = "user/uploadAvatar";
	public static final String PATH_ACCOUNT_DETAIL = "account/detail";

	public static final String KEY_OS = "_os";
	public static final String KEY_VERSION = "_ver";
	public static final String KEY_MID = "_mid";


	public static final String KEY_USERTYPE = "usertype";
	public static final String KEY_TYPE = "type";
	public static final String KEY_USERID = "userid";
	public static final String KEY_NEWSID = "lastnews";
	public static final String KEY_MESSAGEID = "lastmessage";
	public static final String KEY_SCHOOLID = "schoolid";
	public static final String KEY_COACHID = "coachid";


	/**订单状态*/
	public static final String KEY_RESERVATIONSTATE = "reservationstate";
	public static final String KEY_INDEX = "index";
	public static final String KEY_SEQINDEX = "seqindex";
	public static final String KEY_DATE = "date";
	public static final String KEY_PAGE = "page";
	public static final String KEY_COUNT = "count";
	public static final String KEY_COURSE_ID = "courseid";

	public static final String KEY_ORDER_ID = "orderId";
	public static final String KEY_ORDER_MONEY = "money";
	public static final String KEY_ORDER_REMARK = "remark";
	public static final String KEY_ORDER_STATUS = "orderStatus";
	public static final String KEY_CARD_USER = "cardUser";
	public static final String KEY_CARD_NO = "cardNo";
	public static final String KEY_BANK_ADDR = "bankAdd";
	public static final String KEY_MONEY = "money";
	public static final String KEY_BANK_NAME = "bankName";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_RADIUS = "radius";
	public static final String KEY_SCHOOLNAME = "schoolname";
	public static final String KEY_AUTHORIZATION = "authorization";
	public static final String KEY_PRODUCT_ID = "productid";
	public static final String KEY_SEARCH_NAME = "searchname";
	public static final String STUDENT_TYPE = "studenttype";


	public static final String KEY_COACH_ID = "coachid";
	public static final String KEY_YEAR = "year";
	public static final String KEY_MONTH = "month";
	public static final String KEY_IS_SHUTTLE = "is_shuttle";
	public static final String KEY_COURSELIST = "courselist";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_BEGIN_TIME= "begintime";
	public static final String KEY_END_TIME= "endtime";




	public static final boolean DEBUG = Constants.DEBUG;
	public static final int REQ_LEN = 10;//20
	public static final int VOLLEY_TIMEOUT = 10000;

	public static final String DOMAIN_IMGS = "http://7xnjg0.com1.z0.glb.clouddn.com/";





}
