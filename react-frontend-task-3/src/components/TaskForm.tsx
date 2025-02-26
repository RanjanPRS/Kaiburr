import React from "react";
import { Modal, Form, Input, Button, message } from "antd";
import { createTask } from "../api";

interface Props {
  visible: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

const TaskForm: React.FC<Props> = ({ visible, onClose, onSuccess }) => {
  const [form] = Form.useForm();

  const handleCreate = async (values: any) => {
    console.log("Submitting Task:", values);

    try {
      await createTask(values);
      message.success("Task created!");
      onSuccess();
      onClose();
    } catch (error) {
      message.error("Task creation failed.");
    }
  };

  return (
    <Modal visible={visible} onCancel={onClose} onOk={() => form.submit()} title="Create New Task">
      <Form form={form} onFinish={handleCreate}>
        <Form.Item name="id" label="Task ID" rules={[{ required: true, message: "ID is required" }]}>
          <Input placeholder="Enter Task ID" />
        </Form.Item>
        <Form.Item name="name" label="Task Name" rules={[{ required: true, message: "Name is required" }]}>
          <Input placeholder="Enter Task Name" />
        </Form.Item>
        <Form.Item name="owner" label="Owner" rules={[{ required: true, message: "Owner is required" }]}>
          <Input placeholder="Enter Owner Name" />
        </Form.Item>
        <Form.Item name="command" label="Command" rules={[{ required: true, message: "Command is required" }]}>
          <Input placeholder="Enter Shell Command" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default TaskForm;
