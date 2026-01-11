package com._labor.fakecord.model;


public class ChatMessage {  
  private String userName;
  private String content;
  private MessageType type;

  public ChatMessage(){}

  public ChatMessage(String userName, String content, MessageType type) {
    this.userName = userName;
    this.content = content;
    this.type = type;
  }

  public String getUserName() {
    return userName;
  }

  public String getContent() {
    return content;
  }

  public MessageType getType() {
    return type;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setType(MessageType type) {
    this.type = type;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((userName == null) ? 0 : userName.hashCode());
    result = prime * result + ((content == null) ? 0 : content.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    ChatMessage other = (ChatMessage) obj;
    if (userName == null) {
      if (other.userName != null)
        return false;
    } else if (!userName.equals(other.userName))
      return false;
    if (content == null) {
      if (other.content != null)
        return false;
    } else if (!content.equals(other.content))
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ChatMessage [userName=" + userName + ", content=" + content + ", type=" + type + "]";
  }
}
