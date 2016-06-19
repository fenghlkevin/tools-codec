package com.kevin.iestudio.codec.util;

/**
 * 坐标计算器
 * 
 * @author Administrator
 * 
 */
public class CoordinateCalculator {
	
	/**
     * 根据经纬度，获取两点间的距离
     * 
     * @author zhijun.wu
     * @param lng1 经度
     * @param lat1 纬度
     * @param lng2
     * @param lat2
     * @return
     *
     * @date 2011-8-10
     */
    public static double p2pLength(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = lat1 * Math.PI / 180;
        double radLat2 = lat2 * Math.PI / 180;
        double a = radLat1 - radLat2;
        double b = lng1 * Math.PI / 180 - lng2 * Math.PI / 180;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378137.0;// 取WGS84标准参考椭球中的地球长半径(单位:m)
        s = Math.round(s * 10000) / 10000;

        return s;
    }

//	/**
//	 * 计算两点间长度
//	 * 
//	 * @return
//	 */
//	public static double p2pLength(double x1, double y1, double x2, double y2) {
//		 double a=x1-x2;
//		 double b=y1-y2;
//		  return Math.sqrt(a*a+b*b);
////		return 0;
//	}

	/**
	 * 已知3点坐标，计算顶点(x2,y2)据底边的垂直距离(x1,y1--x2,y2)
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param x3
	 * @param y3
	 * @return
	 */
	public static double getVerticalLength(double x1, double y1, double x2, double y2, double x3, double y3) {
		double a = CoordinateCalculator.p2pLength(x1, y1, x2, y2);// 斜边1
		double b = CoordinateCalculator.p2pLength(x2, y2, x3, y3);// 斜边2
		double c = CoordinateCalculator.p2pLength(x1, y1, x3, y3);// 底边长度
		double haflLength = a + b + c / 2.0;

		double sValue = (double) Math.sqrt(haflLength * (haflLength - a) * (haflLength - b) * (haflLength - c));
		return (2.0*sValue)/c;
	}

}
