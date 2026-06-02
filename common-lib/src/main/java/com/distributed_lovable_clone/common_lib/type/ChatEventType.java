package com.distributed_lovable_clone.common_lib.type;

public enum ChatEventType {
    THOUGHT,  //thought for 2s
    MESSAGE, //standard conversational text
    FILE_EDIT, // code generation <file>
    TOOL_LOG // "Reading file tool..."
}
