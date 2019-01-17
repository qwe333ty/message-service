import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Token} from "../../entity/Token";
import {User} from "../../entity/User";
import {Message} from "../../entity/Message";
import {Page} from "../../entity/Page";
import {TokenStorageService} from "../tokenStorage/token-storage.service";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class RequestService {

  userId: number;

  constructor(private http: HttpClient,
              private tokenStorageService: TokenStorageService,
              private router: Router) {
  }

  checkTokenInStorage(): boolean {
    return this.tokenStorageService.getToken() != null;
  }

  getToken(username: string, password: string): Observable<Token> {
    let credentials = {username: username, password: password};
    return this.http.post<Token>('/token/generate-token', credentials);
  }

  findUserByUsername(username: string): Observable<User> {
    return this.http.get<User>('/api/account/user/' + username);
  }

  getMessagePage(userId: number, page: number, size: number, importance: number, starred: number): Observable<Page<Message>> {
    return this.http.get<Page<Message>>('/api/message?page=' + page +
      '&size=' + size + '&user-id=' + userId + '&importance=' + importance +
      '&starred=' + starred);
  }

  countMessagesByUserId(userId: number): Observable<number> {
    return this.http.get<number>('/api/message/count?user-id=' + userId);
  }

  createMessage(message: Message): Observable<Message> {
    return this.http.put<Message>('/api/message', message);
  }

  updateMessage(message: Message): Observable<Message> {
    return this.http.post<Message>('/api/message', message);
  }

  deleteMessage(messageId: number): Observable<number> {
    return this.http.delete<number>('/api/message/' + messageId);
  }

  deleteMessages(messages: Array<Message>): Observable<number> {
    let ids: Array<number> = messages.map(value => value.id);
    return this.http.delete<number>('/api/message/' + ids.join(','));
  }
}
