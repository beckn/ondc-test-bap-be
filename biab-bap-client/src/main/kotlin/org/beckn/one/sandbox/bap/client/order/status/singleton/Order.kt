package org.beckn.one.sandbox.bap.client.order.status.singleton

import org.beckn.one.sandbox.bap.client.shared.dtos.ProtocolOrderObject

class Order private constructor(){
  companion object{
    var order_object : ProtocolOrderObject? = null
  }
}