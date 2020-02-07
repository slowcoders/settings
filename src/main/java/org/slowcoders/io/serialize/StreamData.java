package org.slowcoders.io.serialize;

interface StreamData {

	static final int BOOLEAN 	= 0x00;
	static final int BYTE 		= 0x01;
	static final int CHAR 		= 0x02;
	static final int SHORT 		= 0x03;
	static final int INT 		= 0x04;
	static final int FLOAT 		= 0x05;
	
	static final int STRING 	= 0x06;
	static final int OBJECT 	= 0x07;
	
	static final int LONG 		= 0x08;
	static final int DOUBLE 	= 0x09;
	static final int KEY_VALUE  = 0x0A;
	
	static final int TYPED_OBJECT  		= 0x0B;
	static final int TYPED_COLLECTION  	= 0x0C;
	static final int TYPED_MAP  		= 0x0D;
	
	static final int ARRAY 		= 0x10;
	
	static final int NULL 		= -1;
	
}