// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: compiler/ir/serialization.common/src/KotlinIr.proto

package org.jetbrains.kotlin.backend.common.serialization.proto;

/**
 * Protobuf type {@code org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField}
 */
public final class IrSetField extends
    org.jetbrains.kotlin.protobuf.GeneratedMessageLite implements
    // @@protoc_insertion_point(message_implements:org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField)
    IrSetFieldOrBuilder {
  // Use IrSetField.newBuilder() to construct.
  private IrSetField(org.jetbrains.kotlin.protobuf.GeneratedMessageLite.Builder builder) {
    super(builder);
    this.unknownFields = builder.getUnknownFields();
  }
  private IrSetField(boolean noInit) { this.unknownFields = org.jetbrains.kotlin.protobuf.ByteString.EMPTY;}

  private static final IrSetField defaultInstance;
  public static IrSetField getDefaultInstance() {
    return defaultInstance;
  }

  public IrSetField getDefaultInstanceForType() {
    return defaultInstance;
  }

  private final org.jetbrains.kotlin.protobuf.ByteString unknownFields;
  private IrSetField(
      org.jetbrains.kotlin.protobuf.CodedInputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    initFields();
    int mutable_bitField0_ = 0;
    org.jetbrains.kotlin.protobuf.ByteString.Output unknownFieldsOutput =
        org.jetbrains.kotlin.protobuf.ByteString.newOutput();
    org.jetbrains.kotlin.protobuf.CodedOutputStream unknownFieldsCodedOutput =
        org.jetbrains.kotlin.protobuf.CodedOutputStream.newInstance(
            unknownFieldsOutput, 1);
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!parseUnknownField(input, unknownFieldsCodedOutput,
                                   extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon.Builder subBuilder = null;
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
              subBuilder = fieldAccess_.toBuilder();
            }
            fieldAccess_ = input.readMessage(org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon.PARSER, extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(fieldAccess_);
              fieldAccess_ = subBuilder.buildPartial();
            }
            bitField0_ |= 0x00000001;
            break;
          }
          case 18: {
            org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression.Builder subBuilder = null;
            if (((bitField0_ & 0x00000002) == 0x00000002)) {
              subBuilder = value_.toBuilder();
            }
            value_ = input.readMessage(org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression.PARSER, extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(value_);
              value_ = subBuilder.buildPartial();
            }
            bitField0_ |= 0x00000002;
            break;
          }
          case 26: {
            org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin.Builder subBuilder = null;
            if (((bitField0_ & 0x00000004) == 0x00000004)) {
              subBuilder = origin_.toBuilder();
            }
            origin_ = input.readMessage(org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin.PARSER, extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(origin_);
              origin_ = subBuilder.buildPartial();
            }
            bitField0_ |= 0x00000004;
            break;
          }
        }
      }
    } catch (org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException(
          e.getMessage()).setUnfinishedMessage(this);
    } finally {
      try {
        unknownFieldsCodedOutput.flush();
      } catch (java.io.IOException e) {
      // Should not happen
      } finally {
        unknownFields = unknownFieldsOutput.toByteString();
      }
      makeExtensionsImmutable();
    }
  }
  public static org.jetbrains.kotlin.protobuf.Parser<IrSetField> PARSER =
      new org.jetbrains.kotlin.protobuf.AbstractParser<IrSetField>() {
    public IrSetField parsePartialFrom(
        org.jetbrains.kotlin.protobuf.CodedInputStream input,
        org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
        throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
      return new IrSetField(input, extensionRegistry);
    }
  };

  @java.lang.Override
  public org.jetbrains.kotlin.protobuf.Parser<IrSetField> getParserForType() {
    return PARSER;
  }

  private int bitField0_;
  public static final int FIELD_ACCESS_FIELD_NUMBER = 1;
  private org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon fieldAccess_;
  /**
   * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon field_access = 1;</code>
   */
  public boolean hasFieldAccess() {
    return ((bitField0_ & 0x00000001) == 0x00000001);
  }
  /**
   * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon field_access = 1;</code>
   */
  public org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon getFieldAccess() {
    return fieldAccess_;
  }

  public static final int VALUE_FIELD_NUMBER = 2;
  private org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value_;
  /**
   * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value = 2;</code>
   */
  public boolean hasValue() {
    return ((bitField0_ & 0x00000002) == 0x00000002);
  }
  /**
   * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value = 2;</code>
   */
  public org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression getValue() {
    return value_;
  }

  public static final int ORIGIN_FIELD_NUMBER = 3;
  private org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin origin_;
  /**
   * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin origin = 3;</code>
   */
  public boolean hasOrigin() {
    return ((bitField0_ & 0x00000004) == 0x00000004);
  }
  /**
   * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin origin = 3;</code>
   */
  public org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin getOrigin() {
    return origin_;
  }

  private void initFields() {
    fieldAccess_ = org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon.getDefaultInstance();
    value_ = org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression.getDefaultInstance();
    origin_ = org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin.getDefaultInstance();
  }
  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    if (!hasFieldAccess()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (!hasValue()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (!getFieldAccess().isInitialized()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (!getValue().isInitialized()) {
      memoizedIsInitialized = 0;
      return false;
    }
    if (hasOrigin()) {
      if (!getOrigin().isInitialized()) {
        memoizedIsInitialized = 0;
        return false;
      }
    }
    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(org.jetbrains.kotlin.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    getSerializedSize();
    if (((bitField0_ & 0x00000001) == 0x00000001)) {
      output.writeMessage(1, fieldAccess_);
    }
    if (((bitField0_ & 0x00000002) == 0x00000002)) {
      output.writeMessage(2, value_);
    }
    if (((bitField0_ & 0x00000004) == 0x00000004)) {
      output.writeMessage(3, origin_);
    }
    output.writeRawBytes(unknownFields);
  }

  private int memoizedSerializedSize = -1;
  public int getSerializedSize() {
    int size = memoizedSerializedSize;
    if (size != -1) return size;

    size = 0;
    if (((bitField0_ & 0x00000001) == 0x00000001)) {
      size += org.jetbrains.kotlin.protobuf.CodedOutputStream
        .computeMessageSize(1, fieldAccess_);
    }
    if (((bitField0_ & 0x00000002) == 0x00000002)) {
      size += org.jetbrains.kotlin.protobuf.CodedOutputStream
        .computeMessageSize(2, value_);
    }
    if (((bitField0_ & 0x00000004) == 0x00000004)) {
      size += org.jetbrains.kotlin.protobuf.CodedOutputStream
        .computeMessageSize(3, origin_);
    }
    size += unknownFields.size();
    memoizedSerializedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  protected java.lang.Object writeReplace()
      throws java.io.ObjectStreamException {
    return super.writeReplace();
  }

  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField parseFrom(
      org.jetbrains.kotlin.protobuf.ByteString data)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField parseFrom(
      org.jetbrains.kotlin.protobuf.ByteString data,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField parseFrom(byte[] data)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField parseFrom(
      byte[] data,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField parseFrom(
      java.io.InputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField parseDelimitedFrom(
      java.io.InputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseDelimitedFrom(input, extensionRegistry);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField parseFrom(
      org.jetbrains.kotlin.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return PARSER.parseFrom(input);
  }
  public static org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField parseFrom(
      org.jetbrains.kotlin.protobuf.CodedInputStream input,
      org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return PARSER.parseFrom(input, extensionRegistry);
  }

  public static Builder newBuilder() { return Builder.create(); }
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder(org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField prototype) {
    return newBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() { return newBuilder(this); }

  /**
   * Protobuf type {@code org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField}
   */
  public static final class Builder extends
      org.jetbrains.kotlin.protobuf.GeneratedMessageLite.Builder<
        org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField, Builder>
      implements
      // @@protoc_insertion_point(builder_implements:org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField)
      org.jetbrains.kotlin.backend.common.serialization.proto.IrSetFieldOrBuilder {
    // Construct using org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private void maybeForceBuilderInitialization() {
    }
    private static Builder create() {
      return new Builder();
    }

    public Builder clear() {
      super.clear();
      fieldAccess_ = org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon.getDefaultInstance();
      bitField0_ = (bitField0_ & ~0x00000001);
      value_ = org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression.getDefaultInstance();
      bitField0_ = (bitField0_ & ~0x00000002);
      origin_ = org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin.getDefaultInstance();
      bitField0_ = (bitField0_ & ~0x00000004);
      return this;
    }

    public Builder clone() {
      return create().mergeFrom(buildPartial());
    }

    public org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField getDefaultInstanceForType() {
      return org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField.getDefaultInstance();
    }

    public org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField build() {
      org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField buildPartial() {
      org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField result = new org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField(this);
      int from_bitField0_ = bitField0_;
      int to_bitField0_ = 0;
      if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
        to_bitField0_ |= 0x00000001;
      }
      result.fieldAccess_ = fieldAccess_;
      if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
        to_bitField0_ |= 0x00000002;
      }
      result.value_ = value_;
      if (((from_bitField0_ & 0x00000004) == 0x00000004)) {
        to_bitField0_ |= 0x00000004;
      }
      result.origin_ = origin_;
      result.bitField0_ = to_bitField0_;
      return result;
    }

    public Builder mergeFrom(org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField other) {
      if (other == org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField.getDefaultInstance()) return this;
      if (other.hasFieldAccess()) {
        mergeFieldAccess(other.getFieldAccess());
      }
      if (other.hasValue()) {
        mergeValue(other.getValue());
      }
      if (other.hasOrigin()) {
        mergeOrigin(other.getOrigin());
      }
      setUnknownFields(
          getUnknownFields().concat(other.unknownFields));
      return this;
    }

    public final boolean isInitialized() {
      if (!hasFieldAccess()) {
        
        return false;
      }
      if (!hasValue()) {
        
        return false;
      }
      if (!getFieldAccess().isInitialized()) {
        
        return false;
      }
      if (!getValue().isInitialized()) {
        
        return false;
      }
      if (hasOrigin()) {
        if (!getOrigin().isInitialized()) {
          
          return false;
        }
      }
      return true;
    }

    public Builder mergeFrom(
        org.jetbrains.kotlin.protobuf.CodedInputStream input,
        org.jetbrains.kotlin.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (org.jetbrains.kotlin.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField) e.getUnfinishedMessage();
        throw e;
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon fieldAccess_ = org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon.getDefaultInstance();
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon field_access = 1;</code>
     */
    public boolean hasFieldAccess() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon field_access = 1;</code>
     */
    public org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon getFieldAccess() {
      return fieldAccess_;
    }
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon field_access = 1;</code>
     */
    public Builder setFieldAccess(org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon value) {
      if (value == null) {
        throw new NullPointerException();
      }
      fieldAccess_ = value;

      bitField0_ |= 0x00000001;
      return this;
    }
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon field_access = 1;</code>
     */
    public Builder setFieldAccess(
        org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon.Builder builderForValue) {
      fieldAccess_ = builderForValue.build();

      bitField0_ |= 0x00000001;
      return this;
    }
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon field_access = 1;</code>
     */
    public Builder mergeFieldAccess(org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon value) {
      if (((bitField0_ & 0x00000001) == 0x00000001) &&
          fieldAccess_ != org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon.getDefaultInstance()) {
        fieldAccess_ =
          org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon.newBuilder(fieldAccess_).mergeFrom(value).buildPartial();
      } else {
        fieldAccess_ = value;
      }

      bitField0_ |= 0x00000001;
      return this;
    }
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon field_access = 1;</code>
     */
    public Builder clearFieldAccess() {
      fieldAccess_ = org.jetbrains.kotlin.backend.common.serialization.proto.FieldAccessCommon.getDefaultInstance();

      bitField0_ = (bitField0_ & ~0x00000001);
      return this;
    }

    private org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value_ = org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression.getDefaultInstance();
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value = 2;</code>
     */
    public boolean hasValue() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value = 2;</code>
     */
    public org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression getValue() {
      return value_;
    }
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value = 2;</code>
     */
    public Builder setValue(org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value) {
      if (value == null) {
        throw new NullPointerException();
      }
      value_ = value;

      bitField0_ |= 0x00000002;
      return this;
    }
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value = 2;</code>
     */
    public Builder setValue(
        org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression.Builder builderForValue) {
      value_ = builderForValue.build();

      bitField0_ |= 0x00000002;
      return this;
    }
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value = 2;</code>
     */
    public Builder mergeValue(org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value) {
      if (((bitField0_ & 0x00000002) == 0x00000002) &&
          value_ != org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression.getDefaultInstance()) {
        value_ =
          org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression.newBuilder(value_).mergeFrom(value).buildPartial();
      } else {
        value_ = value;
      }

      bitField0_ |= 0x00000002;
      return this;
    }
    /**
     * <code>required .org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression value = 2;</code>
     */
    public Builder clearValue() {
      value_ = org.jetbrains.kotlin.backend.common.serialization.proto.IrExpression.getDefaultInstance();

      bitField0_ = (bitField0_ & ~0x00000002);
      return this;
    }

    private org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin origin_ = org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin.getDefaultInstance();
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin origin = 3;</code>
     */
    public boolean hasOrigin() {
      return ((bitField0_ & 0x00000004) == 0x00000004);
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin origin = 3;</code>
     */
    public org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin getOrigin() {
      return origin_;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin origin = 3;</code>
     */
    public Builder setOrigin(org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin value) {
      if (value == null) {
        throw new NullPointerException();
      }
      origin_ = value;

      bitField0_ |= 0x00000004;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin origin = 3;</code>
     */
    public Builder setOrigin(
        org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin.Builder builderForValue) {
      origin_ = builderForValue.build();

      bitField0_ |= 0x00000004;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin origin = 3;</code>
     */
    public Builder mergeOrigin(org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin value) {
      if (((bitField0_ & 0x00000004) == 0x00000004) &&
          origin_ != org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin.getDefaultInstance()) {
        origin_ =
          org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin.newBuilder(origin_).mergeFrom(value).buildPartial();
      } else {
        origin_ = value;
      }

      bitField0_ |= 0x00000004;
      return this;
    }
    /**
     * <code>optional .org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin origin = 3;</code>
     */
    public Builder clearOrigin() {
      origin_ = org.jetbrains.kotlin.backend.common.serialization.proto.IrStatementOrigin.getDefaultInstance();

      bitField0_ = (bitField0_ & ~0x00000004);
      return this;
    }

    // @@protoc_insertion_point(builder_scope:org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField)
  }

  static {
    defaultInstance = new IrSetField(true);
    defaultInstance.initFields();
  }

  // @@protoc_insertion_point(class_scope:org.jetbrains.kotlin.backend.common.serialization.proto.IrSetField)
}
