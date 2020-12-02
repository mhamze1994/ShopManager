/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.math.BigDecimal;

/**
 *
 * @author PersianDevStudio
 */
public class AnbarGardaniEntity {

    public long itemId;
    public String desc;
    public BigDecimal count1;
    public BigDecimal count2;
    public BigDecimal count3;

    public AnbarGardaniEntity(long itemId, String desc, BigDecimal count1, BigDecimal count2, BigDecimal count3) {
        this.itemId = itemId;
        this.desc = desc;
        this.count1 = count1 == null ? BigDecimal.ZERO : count1;
        this.count2 = count2 == null ? BigDecimal.ZERO : count2;
        this.count3 = count3 == null ? BigDecimal.ZERO : count3;
    }
}
