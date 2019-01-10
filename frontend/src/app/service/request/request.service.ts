import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Token} from "../../entity/Token";
import {User} from "../../entity/User";

@Injectable({
  providedIn: 'root'
})
export class RequestService {

  constructor(private http: HttpClient) {
  }

  getToken(username: string, password: string): Observable<Token> {
    let credentials = {username: username, password: password};
    return this.http.post<Token>('/token/generate-token', credentials);
  }

  findUserByUsername(username: string) {
    return this.http.get<User>('/api/account/user/' + username);
  }
}
