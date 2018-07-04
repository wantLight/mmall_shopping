package com.mmall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by xyzzg on 2018/6/15.
 */
public class CartVo {

    private List<CartProductVo> productVoList;
    private BigDecimal cartTotalPrice;
    private Boolean allChecked;//是否已经都勾选
    private String imageHost;

    public List<CartProductVo> getProductVoList() {
        return productVoList;
    }

    public void setProductVoList(List<CartProductVo> productVoList) {
        this.productVoList = productVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}
