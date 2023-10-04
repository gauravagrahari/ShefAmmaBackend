package com.shefamma.shefamma.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderWithAddress {
    private OrderEntity order;
    private AdressSubEntity hostAddress;
    private AdressSubEntity guestAddress;
}
