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
    public static final int SERVER_INFORM_CLIENT_NUMBER = 3;

    // server gửi cập nhật các client đang rảnh
    public static final int SERVER_INFORM_CLIENT_WAITING = 4;

    // client gửi thông báo đăng nhập thành công, server cập nhật danh sách client rảnh
    public static final int LOGIN_SUCCESSFUL = 5;

    // 1 client request mời chơi client khác
    public static final int SEND_PLAY_REQUEST = 6;
    public static final int SERVER_SEND_PLAY_REQUEST_ERROR = 7;

    // server gửi request này cho client được mời
    public static final int RECEIVE_PLAY_REQUEST = 8;

    // client kia chấp nhận
    public static final int ACCEPTED_PLAY_REQUEST = 9;

    // server gửi request này cho cả 2 khi người kia chấp nhận
    public static final int SERVER_SET_GAME_READY = 10;

    // client kia từ chối
    public static final int REJECTED_PLAY_REQUEST = 11;

    // ?
    public static final int SERVER_REJECTED_PLAY_REQUEST = 12;

    // client gửi request sẵn sàng (đã xếp tàu xong), kèm data chính là arraylist string vị trí các tàu
    public static final int READY_PLAY_GAME = 13;

    // server nhận được danh sách tàu của 1 client thì gửi cho client kia để nó lưu
    public static final int SERVER_TRANSFER_POSITION_ENEMY_SHIP = 14;

    // server random chọn 1 trong 2 đi trước sau giai đoạn xếp tàu
    public static final int SERVER_RANDOM_TURN = 15;
    public static final int SERVER_RANDOM_NOT_TURN = 16;

    // server kiểm soát lượt trong lúc bắn
    public static final int SERVER_CHOOSE_TURN = 17;
    public static final int SERVER_CHOOSE_NOT_TURN = 18;

    // cả 2 được xác định đã sẵn sàng , server gửi cho cả 2 bắt đầu game
    public static final int SERVER_START_PLAY_GAME = 19;

    // client 1 được xác định bắn hụt, gửi kèm string location
    public static final int SHOOT_FAILTURE = 20;
    // server gửi bắn hụt đến enemy để vẽ, kèm string location
    public static final int SERVER_TRANSFER_SHOOT_FAILTURE = 21;

    // client gửi request này tức logout ở mainfrm
    public static final int EXIT_MAIN_FORM = 22;

    // luồng 1 client bị ngắt đột ngột khi chơi ?
    public static final int SERVER_DISCONNECTED_CLIENT_ERROR = 23;

    // client yêu cầu cập nhật lại danh sách client rảnh
    public static final int UPDATE_WAITING_LIST_REQUEST = 24;

    // client 1 được xác định bắn trúng 1 điểm, gửi kèm string location
    public static final int SHOOT_HIT_POINT = 25;
    // server gửi bắn trúng 1 điểm đến enemy để vẽ, kèm string location
    public static final int SERVER_TRANSFER_SHOOT_HIT_POINT = 26;

    // client 1 được xác định bắn trúng và phá huỷ 1 con tàu, gửi kèm list string location tàu đó
    public static final int SHOOT_HIT_SHIP = 27;
    // server gửi bắn trúng và phá huỷ 1 con tàu cho enemy để vẽ, gửi kèm list string location tàu đó
    public static final int SERVER_TRANSFER_SHOOT_HIT_SHIP = 28;

    // client 1 được xác định bắn trúng, phá huỷ 1 con tàu và thắng, gửi kèm list string location tàu đó
    public static final int SHOOT_HIT_WIN = 29;
    // server gửi bắn trúng và phá huỷ 1 con tàu cho enemy để vẽ, gửi kèm list string location tàu đó, kết quả thua
    public static final int SERVER_TRANSFER_LOSE = 30;

    // client 1 được xác định hết thời gian mà không bắn, mất lượt
    public static final int SHOOT_MISS_TURN = 31;
    // server gửi mất lượt về cho client kia
    public static final int SERVER_TRANSFER_SHOOT_MISS_TURN = 32;

    // server gửi kết quả hiển thị cho result form
    public static final int GET_RESULT = 33;
    public static final int SERVER_SEND_RESULT = 34;

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
