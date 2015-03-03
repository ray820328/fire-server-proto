// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: com/ray/message/proto/client/net/heart_beat_msg.proto

package com.ray.message.java.client.net;

public final class HeartBeatMsgProto {
  private HeartBeatMsgProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface HeartBeatMsgOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
    
    // required int32 serverTime = 1;
    boolean hasServerTime();
    int getServerTime();
  }
  public static final class HeartBeatMsg extends
      com.google.protobuf.GeneratedMessage
      implements HeartBeatMsgOrBuilder {
    // Use HeartBeatMsg.newBuilder() to construct.
    private HeartBeatMsg(Builder builder) {
      super(builder);
    }
    private HeartBeatMsg(boolean noInit) {}
    
    private static final HeartBeatMsg defaultInstance;
    public static HeartBeatMsg getDefaultInstance() {
      return defaultInstance;
    }
    
    public HeartBeatMsg getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.ray.message.java.client.net.HeartBeatMsgProto.internal_static_com_ray_message_proto_client_net_HeartBeatMsg_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.ray.message.java.client.net.HeartBeatMsgProto.internal_static_com_ray_message_proto_client_net_HeartBeatMsg_fieldAccessorTable;
    }
    
    private int bitField0_;
    // required int32 serverTime = 1;
    public static final int SERVERTIME_FIELD_NUMBER = 1;
    private int serverTime_;
    public boolean hasServerTime() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public int getServerTime() {
      return serverTime_;
    }
    
    private void initFields() {
      serverTime_ = 0;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;
      
      if (!hasServerTime()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeInt32(1, serverTime_);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, serverTime_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }
    
    public static com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsgOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.ray.message.java.client.net.HeartBeatMsgProto.internal_static_com_ray_message_proto_client_net_HeartBeatMsg_descriptor;
      }
      
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.ray.message.java.client.net.HeartBeatMsgProto.internal_static_com_ray_message_proto_client_net_HeartBeatMsg_fieldAccessorTable;
      }
      
      // Construct using com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }
      
      public Builder clear() {
        super.clear();
        serverTime_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg.getDescriptor();
      }
      
      public com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg getDefaultInstanceForType() {
        return com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg.getDefaultInstance();
      }
      
      public com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg build() {
        com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg buildPartial() {
        com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg result = new com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.serverTime_ = serverTime_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg) {
          return mergeFrom((com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg other) {
        if (other == com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg.getDefaultInstance()) return this;
        if (other.hasServerTime()) {
          setServerTime(other.getServerTime());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public final boolean isInitialized() {
        if (!hasServerTime()) {
          
          return false;
        }
        return true;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              onChanged();
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                onChanged();
                return this;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              serverTime_ = input.readInt32();
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // required int32 serverTime = 1;
      private int serverTime_ ;
      public boolean hasServerTime() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public int getServerTime() {
        return serverTime_;
      }
      public Builder setServerTime(int value) {
        bitField0_ |= 0x00000001;
        serverTime_ = value;
        onChanged();
        return this;
      }
      public Builder clearServerTime() {
        bitField0_ = (bitField0_ & ~0x00000001);
        serverTime_ = 0;
        onChanged();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:com.ray.message.proto.client.net.HeartBeatMsg)
    }
    
    static {
      defaultInstance = new HeartBeatMsg(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:com.ray.message.proto.client.net.HeartBeatMsg)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_com_ray_message_proto_client_net_HeartBeatMsg_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_com_ray_message_proto_client_net_HeartBeatMsg_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n5com/ray/message/proto/client/net/heart" +
      "_beat_msg.proto\022 com.ray.message.proto.c" +
      "lient.net\"\"\n\014HeartBeatMsg\022\022\n\nserverTime\030" +
      "\001 \002(\005B4\n\037com.ray.message.java.client.net" +
      "B\021HeartBeatMsgProto"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_com_ray_message_proto_client_net_HeartBeatMsg_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_com_ray_message_proto_client_net_HeartBeatMsg_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_com_ray_message_proto_client_net_HeartBeatMsg_descriptor,
              new java.lang.String[] { "ServerTime", },
              com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg.class,
              com.ray.message.java.client.net.HeartBeatMsgProto.HeartBeatMsg.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  // @@protoc_insertion_point(outer_class_scope)
}
