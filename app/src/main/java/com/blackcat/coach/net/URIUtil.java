package com.blackcat.coach.net;

import android.content.Context;
import android.util.Log;

import com.blackcat.coach.CarCoachApplication;
import com.blackcat.coach.utils.BaseUtils;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


public class URIUtil {

	public static void fillBasicParams(Context context, List<NameValuePair> pairs) {
		if (pairs != null) {
			pairs.add(new BasicNameValuePair(NetConstants.KEY_OS, "2"));
			pairs.add(new BasicNameValuePair(NetConstants.KEY_VERSION, String.valueOf(BaseUtils.getVersionCode(context))));
			pairs.add(new BasicNameValuePair(NetConstants.KEY_MID, BaseUtils.getTokenID()));
		}
	}

	public static URI getLoginUri() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_LOGIN, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getRegister(String mobile, String authCode, String pwd, String invite) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
        try {
            URI uri = URIUtils.createURI(NetConstants.HTTP,
                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
                    NetConstants.PATH_REGISTER, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
            logRequestUri(uri);
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	public static URI getFindPwd() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_UPDATE_PWD, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getFeedbackURI() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_UPDATE_FEEDBACK, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getSendSms(String mobile) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_MSG_CODE + mobile, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getApplyVerify() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_APPLY_VERIFY, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getReservationList(String coachid, int pos) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		params.add(new BasicNameValuePair(NetConstants.KEY_COACHID, coachid));
//		params.add(new BasicNameValuePair(NetConstants.KEY_COACHID, "5616352721ec29041a9af889"));

		params.add(new BasicNameValuePair(NetConstants.KEY_INDEX, String.valueOf(pos)));
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_RESERVATION_LIST, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getScheduleList(String coachid, int pos, String date) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		params.add(new BasicNameValuePair(NetConstants.KEY_COACHID, coachid));
//		params.add(new BasicNameValuePair(NetConstants.KEY_COACHID, "5616352721ec29041a9af889"));
		params.add(new BasicNameValuePair(NetConstants.KEY_INDEX, String.valueOf(pos)));
		params.add(new BasicNameValuePair(NetConstants.KEY_DATE, date));
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_SCHEDULE_LIST, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getStudentsList(String coachid, int pos) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		params.add(new BasicNameValuePair(NetConstants.KEY_COACHID, coachid));
//		params.add(new BasicNameValuePair(NetConstants.KEY_COACHID, "5616352721ec29041a9af889"));

		params.add(new BasicNameValuePair(NetConstants.KEY_INDEX, String.valueOf(pos)));
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_STUDENTS, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getReservationInfo(String reservationId) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_RESERVATIONINFO + reservationId, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getUpdateMobile(String authCode, String mobile) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_UPDATE_MOBILE, URLEncodedUtils.format(params, NetConstants.ENCODING), null);

			return uri;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getHandleClass(String coachid, String reservationid, int handletype,
									 String cancelreason, String cancelcontent) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_HANDLE_CLASS, URLEncodedUtils.format(params, NetConstants.ENCODING), null);

			return uri;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getFinishClass() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_FINISH_CLASS, URLEncodedUtils.format(params, NetConstants.ENCODING), null);

			return uri;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getCoachComment() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_COACH_COMMENT, URLEncodedUtils.format(params, NetConstants.ENCODING), null);

			return uri;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getCommentList(String userId, int page) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_COMMENTS + userId + "/" + page, URLEncodedUtils.format(params, NetConstants.ENCODING), null);

			return uri;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getFieldsList(String schoolid) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		params.add(new BasicNameValuePair(NetConstants.KEY_SCHOOLID, schoolid));
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_TRAININGFIELD, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getSubjectsList() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_SUBJECTS, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI updateCoachInfo() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_UPDATE_COACHINFO, URLEncodedUtils.format(params, NetConstants.ENCODING), null);

			return uri;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getNearbySchoolList(String latitude, String longitude, String radius) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		params.add(new BasicNameValuePair(NetConstants.KEY_LATITUDE, latitude));
		params.add(new BasicNameValuePair(NetConstants.KEY_LONGITUDE, longitude));
		params.add(new BasicNameValuePair(NetConstants.KEY_RADIUS, radius));
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_NEARBYSCHOOL, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getSchoolbyNameList(String schoolname) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		params.add(new BasicNameValuePair(NetConstants.KEY_SCHOOLNAME, schoolname));
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_SCHOOLBYNAME, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getUserInfo(String userId) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		params.add(new BasicNameValuePair(NetConstants.KEY_USERID, userId));
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_STUDENTINFO, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getClassesList() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_CLASSTYPE, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getSaveClasses() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_SAVE_CLASSES, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getSetWorkTime() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_SET_WORKTIME, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getSetVacation() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_SET_VACATION, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getQiniuToken() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_QINIU_UPTOKEN, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getCoachInfo(String userId) {
        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair(NetConstants.KEY_USERID, userId));
		fillBasicParams(CarCoachApplication.getInstance(), params);
        try {
            URI uri = URIUtils.createURI(NetConstants.HTTP,
                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
                    NetConstants.PATH_GET_COACHINFO, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
            logRequestUri(uri);
            return uri;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

	public static URI getMyWallet(String userId, int seq, int count) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(NetConstants.KEY_USERID, userId));
		params.add(new BasicNameValuePair(NetConstants.KEY_SEQINDEX, String.valueOf(seq)));
		params.add(new BasicNameValuePair(NetConstants.KEY_COUNT, String.valueOf(count)));
		params.add(new BasicNameValuePair(NetConstants.KEY_USERTYPE, "2"));
		fillBasicParams(CarCoachApplication.getInstance(), params);

		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_MY_WALLET, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getMallProduct() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_MALL_PRODUCT, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getDetailProduct(String productId) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(NetConstants.KEY_PRODUCT_ID, productId));
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_GET_DETAIL_PRODUCT, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static URI getBuyProduct() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		fillBasicParams(CarCoachApplication.getInstance(), params);
		try {
			URI uri = URIUtils.createURI(NetConstants.HTTP,
					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
					NetConstants.PATH_BUY_PRODUCT, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
			logRequestUri(uri);
			return uri;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	public static URI getFeedback(String token, String content, String contact) {
//		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//		params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new FeedbackParameter(token, content, contact).combineParamsInJson()));
//		try {
//			URI uri = URIUtils.createURI(NetConstants.HTTP,
//					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//					NetConstants.PATH_FEEDBACK, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//			logRequestUri(uri);
//			return uri;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public static URI getRefreshToken(String token) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new TokenParmeter(token).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_REFRESH_TOKEN, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI getPushTags(String token) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new TokenParmeter(token).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_PUSH_TAGS, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//
//	public static URI getUploadAvatar(String token, String img) {
//	    ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new UploadImageParameter(token, img).combineParamsInJson()));
//		try {
//			URI uri = URIUtils.createURI(NetConstants.HTTP,
//					NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//					NetConstants.PATH_UPLOAD_AVATAR, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//			logRequestUri(uri);
//			return uri;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public static URI getQiniuToken(String token) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new TokenParmeter(token).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_QINIU_TOKEN, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI upload2Qiniu(String token, String img) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new UploadImageParameter(token, img).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_UPLOAD_AVATAR, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI getAccountInfo(String token) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new TokenParmeter(token).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_ACCOUNT_DETAIL, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI getLatestCount(String token, long timestamp) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new TimestampParameter(token, timestamp).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_LATEST_COUNT, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI getLatestList(String token, int page, int count) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new PageCountParameter(token, page, count).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_LATEST_LIST, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI getMyOrderList(String token, int page, int count, int status) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new MyOrdersParameter(token, page, count, status).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_MY_ORDERS, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI getOrderDetail(String token, String id) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new OrderIdParameter(token, id).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_ORDER_DETAIL, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI getGrabOrder(String token, String id, int money) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new AdvanceRobParameter(token, id, money).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_ROB, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI getCancelExcuse(String token, String id) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new OrderIdParameter(token, id).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_CANCEL_EXCUSE, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI getSubmitExcuse(String token, String id, String remark) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new RemarkParameter(token, id, remark).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_ADVANCE_ROB_CANCEL, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//	}
//
//	public static URI getSubmitCert(String token, String id, String img) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new SubCertParameter(token, img, id).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_SUBMIT_CERT, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI getCash(String token, String usr, String cardNo, String bankName, String bankAddr, int money) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new WithdrawalParameter(token, usr, cardNo, bankName, bankAddr, money).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_CASH, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//	public static URI getCashRecords(String token, int page, int count) {
//        ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
//        params.add(new BasicNameValuePair(NetConstants.KEY_CONTENT, new PageCountParameter(token, page, count).combineParamsInJson()));
//        try {
//            URI uri = URIUtils.createURI(NetConstants.HTTP,
//                    NetConstants.HOSTNAME, NetConstants.DEFAULT_PORT,
//                    NetConstants.PATH_CASH_RECORD, URLEncodedUtils.format(params, NetConstants.ENCODING), null);
//            logRequestUri(uri);
//            return uri;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
	
	public static void logRequestUri(URI uri) {
		if(NetConstants.DEBUG && uri != null) {
			try {
				Log.d("http", uri.toURL().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
