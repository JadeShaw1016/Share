package com.example.administrator.share.util;

import java.util.List;

public class BingPic {
    /**
     * images : [{"startdate":"20200720","fullstartdate":"202007201600","enddate":"20200721","url":"/th?id=OHR.DinantBelgium_ZH-CN0913727176_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp","urlbase":"/th?id=OHR.DinantBelgium_ZH-CN0913727176","copyright":"迪南镇和默兹河，比利时纳穆尔 (© Kadagan/Shutterstock)","copyrightlink":"https://www.bing.com/search?q=%E8%BF%AA%E5%8D%97%E9%95%87&form=hpcapt&mkt=zh-cn","title":"","quiz":"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20200720_DinantBelgium%22&FORM=HPQUIZ","wp":true,"hsh":"f44144bfcf0890630031e2b7d1dfb071","drk":1,"top":1,"bot":1,"hs":[]},{"startdate":"20200719","fullstartdate":"202007191600","enddate":"20200720","url":"/th?id=OHR.EarthriseSequence_ZH-CN0750195611_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp","urlbase":"/th?id=OHR.EarthriseSequence_ZH-CN0750195611","copyright":"穿过月球上史密斯海的\u201c地出\u201d (© Image Science and Analysis Laboratory, NASA-Johnson Space Center)","copyrightlink":"https://www.bing.com/search?q=%E5%8F%B2%E5%AF%86%E6%96%AF%E6%B5%B7&form=hpcapt&mkt=zh-cn","title":"","quiz":"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20200719_EarthriseSequence%22&FORM=HPQUIZ","wp":true,"hsh":"9c767b14e27527b152d66faabe9edb36","drk":1,"top":1,"bot":1,"hs":[]},{"startdate":"20200718","fullstartdate":"202007181600","enddate":"20200719","url":"/th?id=OHR.GrandCanalGondolas_ZH-CN0542933448_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp","urlbase":"/th?id=OHR.GrandCanalGondolas_ZH-CN0542933448","copyright":"大运河和安康圣母圣殿，意大利威尼斯 (© Jim Richardson/Offset by Shutterstock)","copyrightlink":"https://www.bing.com/search?q=%E6%9B%B4%E5%90%8D&form=hpcapt&mkt=zh-cn","title":"","quiz":"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20200718_GrandCanalGondolas%22&FORM=HPQUIZ","wp":true,"hsh":"bffd409f66bd1c275ac81c1865487861","drk":1,"top":1,"bot":1,"hs":[]},{"startdate":"20200717","fullstartdate":"202007171600","enddate":"20200718","url":"/th?id=OHR.NineSpotted_ZH-CN0422284522_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp","urlbase":"/th?id=OHR.NineSpotted_ZH-CN0422284522","copyright":"九斑蛾，瑞士 (© Thomas Marent/Minden Pictures)","copyrightlink":"https://www.bing.com/search?q=%E4%B9%9D%E6%96%91%E8%9B%BE&form=hpcapt&mkt=zh-cn","title":"","quiz":"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20200717_NineSpotted%22&FORM=HPQUIZ","wp":true,"hsh":"fe9ea5c15fcb3687afcbfe801e9a7d99","drk":1,"top":1,"bot":1,"hs":[]},{"startdate":"20200716","fullstartdate":"202007161600","enddate":"20200717","url":"/th?id=OHR.HappyBalloon_ZH-CN0324866466_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp","urlbase":"/th?id=OHR.HappyBalloon_ZH-CN0324866466","copyright":"笑脸热气球 (© Leonsbox/Getty Images Plus)","copyrightlink":"https://www.bing.com/search?q=%E7%83%AD%E6%B0%94%E7%90%83&form=hpcapt&mkt=zh-cn","title":"","quiz":"/search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20200716_HappyBalloon%22&FORM=HPQUIZ","wp":true,"hsh":"2daaf9b6bfbd81a8dd8dd8130a1fc6c1","drk":1,"top":1,"bot":1,"hs":[]}]
     * tooltips : {"loading":"正在加载...","previous":"上一个图像","next":"下一个图像","walle":"此图片不能下载用作壁纸。","walls":"下载今日美图。仅限用作桌面壁纸。"}
     */

    private TooltipsBean tooltips;
    private List<ImagesBean> images;

    public TooltipsBean getTooltips() {
        return tooltips;
    }

    public void setTooltips(TooltipsBean tooltips) {
        this.tooltips = tooltips;
    }

    public List<ImagesBean> getImages() {
        return images;
    }

    public void setImages(List<ImagesBean> images) {
        this.images = images;
    }

    public static class TooltipsBean {
        /**
         * loading : 正在加载...
         * previous : 上一个图像
         * next : 下一个图像
         * walle : 此图片不能下载用作壁纸。
         * walls : 下载今日美图。仅限用作桌面壁纸。
         */

        private String loading;
        private String previous;
        private String next;
        private String walle;
        private String walls;

        public String getLoading() {
            return loading;
        }

        public void setLoading(String loading) {
            this.loading = loading;
        }

        public String getPrevious() {
            return previous;
        }

        public void setPrevious(String previous) {
            this.previous = previous;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
        }

        public String getWalle() {
            return walle;
        }

        public void setWalle(String walle) {
            this.walle = walle;
        }

        public String getWalls() {
            return walls;
        }

        public void setWalls(String walls) {
            this.walls = walls;
        }
    }

    public static class ImagesBean {
        /**
         * startdate : 20200720
         * fullstartdate : 202007201600
         * enddate : 20200721
         * url : /th?id=OHR.DinantBelgium_ZH-CN0913727176_1920x1080.jpg&rf=LaDigue_1920x1080.jpg&pid=hp
         * urlbase : /th?id=OHR.DinantBelgium_ZH-CN0913727176
         * copyright : 迪南镇和默兹河，比利时纳穆尔 (© Kadagan/Shutterstock)
         * copyrightlink : https://www.bing.com/search?q=%E8%BF%AA%E5%8D%97%E9%95%87&form=hpcapt&mkt=zh-cn
         * title :
         * quiz : /search?q=Bing+homepage+quiz&filters=WQOskey:%22HPQuiz_20200720_DinantBelgium%22&FORM=HPQUIZ
         * wp : true
         * hsh : f44144bfcf0890630031e2b7d1dfb071
         * drk : 1
         * top : 1
         * bot : 1
         * hs : []
         */

        private String startdate;
        private String fullstartdate;
        private String enddate;
        private String url;
        private String urlbase;
        private String copyright;
        private String copyrightlink;
        private String title;
        private String quiz;
        private boolean wp;
        private String hsh;
        private int drk;
        private int top;
        private int bot;
        private List<?> hs;

        public String getStartdate() {
            return startdate;
        }

        public void setStartdate(String startdate) {
            this.startdate = startdate;
        }

        public String getFullstartdate() {
            return fullstartdate;
        }

        public void setFullstartdate(String fullstartdate) {
            this.fullstartdate = fullstartdate;
        }

        public String getEnddate() {
            return enddate;
        }

        public void setEnddate(String enddate) {
            this.enddate = enddate;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrlbase() {
            return urlbase;
        }

        public void setUrlbase(String urlbase) {
            this.urlbase = urlbase;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        public String getCopyrightlink() {
            return copyrightlink;
        }

        public void setCopyrightlink(String copyrightlink) {
            this.copyrightlink = copyrightlink;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getQuiz() {
            return quiz;
        }

        public void setQuiz(String quiz) {
            this.quiz = quiz;
        }

        public boolean isWp() {
            return wp;
        }

        public void setWp(boolean wp) {
            this.wp = wp;
        }

        public String getHsh() {
            return hsh;
        }

        public void setHsh(String hsh) {
            this.hsh = hsh;
        }

        public int getDrk() {
            return drk;
        }

        public void setDrk(int drk) {
            this.drk = drk;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getBot() {
            return bot;
        }

        public void setBot(int bot) {
            this.bot = bot;
        }

        public List<?> getHs() {
            return hs;
        }

        public void setHs(List<?> hs) {
            this.hs = hs;
        }
    }
}
