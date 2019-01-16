import {User} from "./User";

export class Message {
  id: number;
  from: User;
  to: User;
  sent: Date;
  subject: string;
  messageText: string;
  sendStatus: number;
  readStatus: boolean;
  starred: boolean;
  important: boolean;
}
