/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package shared.model;

import java.io.Serializable;

public class ObjectWrapper implements Serializable {

    // client gửi request login
    public static final int LOGIN_USER = 1;

    // server respone request login
    public static final int SERVER_LOGIN_USER = 2;

//    public static final int REPLY_MAIN_USER = 3;
//    public static final int EDIT_CUSTOMER = 3;
//    public static final int SEARCH_CUSTOMER_BY_NAME = 5;
//    public static final int REPLY_SEARCH_CUSTOMER = 6;
    // server gửi cập nhật số number client online
    public static final int SERVER_INFORM_CLIENT_NUMBER = 7;

    // server gửi cập nhật các client đang rảnh
    public static final int SERVER_INFORM_CLIENT_WAITING = 8;

    // client gửi thông báo đăng nhập thành công, server cập nhật danh sách client rảnh
    public static final int LOGIN_SUCCESSFUL = 9;

    // 1 client request mời chơi client khác
    public static final int SEND_PLAY_REQUEST = 10;
    public static final int SERVER_SEND_PLAY_REQUEST_ERROR = 25;

    // server gửi request này cho client được mời
    public static final int RECEIVE_PLAY_REQUEST = 24;

    // client kia chấp nhận
    public static final int ACCEPTED_PLAY_REQUEST = 11;

    // server gửi request này cho cả 2 khi người kia chấp nhận
    public static final int SERVER_SET_GAME_READY = 12;

    // client kia từ chối
    public static final int REJECTED_PLAY_REQUEST = 13;

    // ?
    public static final int SERVER_REJECTED_PLAY_REQUEST = 14;

    // client gửi request sẵn sàng (đã xếp tàu xong), kèm data chính là string vị trí các tàu
    public static final int READY_PLAY_GAME = 15;
    
    // server nhận được danh sách tàu của 1 client thì gửi cho client kia để nó lưu
    public static final int SERVER_TRANSFER_POSITION_ENEMY_SHIP = 18;

    // ?
    public static final int SERVER_SEND_READY_GAME_NOTI = 16;

    // server random và chọn 1 trong 2 đi trước, đi trước được nhận 17, sau nhận 26
    public static final int SERVER_RANDOM_TURN = 17;
    public static final int SERVER_RANDOM_NOT_TURN = 26;

    // cả 2 được xác định đã sẵn sàng , server gửi cho cả 2 bắt đầu game
    public static final int SERVER_START_PLAY_GAME = 27;

    // server gửi tin nhắn (log bắn tàu) qua lại 2 bên
    public static final int SERVER_TRANSFER_MESSAGE_IN_PLAY = 19;

    // client gửi request này tức logout ở mainfrm
    public static final int EXIT_MAIN_FORM = 20;

    // luồng 1 client bị ngắt đột ngột khi chơi ?
    public static final int SERVER_DISCONNECTED_CLIENT_ERROR = 21;

    // client yêu cầu cập nhật lại danh sách client rảnh
    public static final int UPDATE_WAITING_LIST_REQUEST = 22;

    // ?
    public static final int END_GAME = 23; // connect error or out game ?

    // ?
    public static final int ENEMY_IN_GAME_ERROR = 28;

    // ?
    public static final int EXIT_WHEN_PLAYING = 29;

    private int performative;
    private Object data;

    public ObjectWrapper() {
        super();
    }

    public ObjectWrapper(int performative, Object data) {
        super();
        this.performative = performative;
        this.data = data;
    }

    public ObjectWrapper(int performative) {
        this.performative = performative;
    }

    public int getPerformative() {
        return performative;
    }

    public void setPerformative(int performative) {
        this.performative = performative;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ObjectWrapper{" + "performative=" + performative + ", data=" + data + '}';
    }
}
