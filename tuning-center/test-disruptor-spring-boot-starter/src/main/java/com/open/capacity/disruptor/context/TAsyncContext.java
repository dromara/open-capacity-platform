package com.open.capacity.disruptor.context;


import com.open.capacity.common.disruptor.AsyncContext;

import lombok.Data;

@Data
public class TAsyncContext extends AsyncContext {
	
	private javax.servlet.AsyncContext asyncContext ;
	
	private String msg ;

	
	
}
