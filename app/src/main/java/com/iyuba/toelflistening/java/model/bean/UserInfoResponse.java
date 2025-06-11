package com.iyuba.toelflistening.java.model.bean;

import com.google.gson.annotations.SerializedName;

public class UserInfoResponse {

    /**
     * albums = 0;//专辑数
     * allThumbUp = 552;//总的点赞数
     * amount = 24;//爱语币数量
     * bio = "是啊真的好想好好学学吧”;//一句话简介
     * blogs = 0;//日志数
     * contribute = 0;
     * credits = 10304;//积分数  等同于下面的 jiFen
     * jiFen = 10304;//积分数   等同于上面的credits
     * distance = "";
     * doings = 47;//说说数目
     * email = "chenjinrong@iyuba.cn”;//注册邮箱
     * expireTime = 1689400894;//会员到期时间
     * follower = 222;//粉丝数
     * following = 50;//关注数
     * friends = 0;
     * gender = 1;//性别
     * icoins = 10304;//积分
     * message = "";
     * "middle_url" = "head/2021/2/5/19/3/52/59f71a19-77b9-4093-ae47-9781fc1bb014-m.jpg”;//头像
     * mobile = 00000000000;//手机号
     * money = 315;//钱包, 单位是分
     * nickname = "每个月”;//昵称
     * posts = 0;
     * relation = 0;
     * result = 201;
     * sharings = 0;
     * shengwang = 0;//声望数
     * text = "good morning”;//最近的一个说说文字内容
     * username = Jinrong110;//用户名
     * views = 27571;//多少人看我的主页
     * vipStatus = 1;//会员状态
     * isteacher 是否是老师
     */
    public String message;// 返回信息
    public String result;// 返回代码
    public String username;// 用户名
    @SerializedName("amount")
    public String iyubi;
    @SerializedName("credits")
    public String jifen = "0";
    public String position;
    public String expireTime;
    public String middle_url;
    public String vipStatus;

    public String albums = "0";
    public String gender = "0";
    public String distance = "0";
    public String blogs = "0";
    public String contribute = "0";
    public String shengwang = "0";
    public String bio = "0";
    public String posts = "0";
    public String relation = "0";
    public String nickname = "0";
    public String email = "0";
    public String views = "0";
    public String follower = "0";
    public String mobile = "0";
    public String allThumbUp = "0";
    public String icoins = "0";
    public String friends = "0";
    public String doings = "0";
    public String money = "0";
    public String following = "0";
    public String sharings = "0";
    public String isteacher = "0";


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIyubi() {
        return iyubi;
    }

    public void setIyubi(String iyubi) {
        this.iyubi = iyubi;
    }

    public String getJifen() {
        return jifen;
    }

    public void setJifen(String jifen) {
        this.jifen = jifen;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getMiddle_url() {
        return middle_url;
    }

    public void setMiddle_url(String middle_url) {
        this.middle_url = middle_url;
    }

    public String getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(String vipStatus) {
        this.vipStatus = vipStatus;
    }

    public String getAlbums() {
        return albums;
    }

    public void setAlbums(String albums) {
        this.albums = albums;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getBlogs() {
        return blogs;
    }

    public void setBlogs(String blogs) {
        this.blogs = blogs;
    }

    public String getContribute() {
        return contribute;
    }

    public void setContribute(String contribute) {
        this.contribute = contribute;
    }

    public String getShengwang() {
        return shengwang;
    }

    public void setShengwang(String shengwang) {
        this.shengwang = shengwang;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAllThumbUp() {
        return allThumbUp;
    }

    public void setAllThumbUp(String allThumbUp) {
        this.allThumbUp = allThumbUp;
    }

    public String getIcoins() {
        return icoins;
    }

    public void setIcoins(String icoins) {
        this.icoins = icoins;
    }

    public String getFriends() {
        return friends;
    }

    public void setFriends(String friends) {
        this.friends = friends;
    }

    public String getDoings() {
        return doings;
    }

    public void setDoings(String doings) {
        this.doings = doings;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getSharings() {
        return sharings;
    }

    public void setSharings(String sharings) {
        this.sharings = sharings;
    }

    public String getIsteacher() {
        return isteacher;
    }

    public void setIsteacher(String isteacher) {
        this.isteacher = isteacher;
    }
}
