import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ShareResourceService {

  // Observable string sources
  private authorizedSource = new BehaviorSubject(false);
  private userIdSource = new BehaviorSubject(-1);

  // Observable string streams
  authorized$ = this.authorizedSource.asObservable();
  userId$ = this.userIdSource.asObservable();

  nextAuthorizedObs(bln: boolean) {
    this.authorizedSource.next(bln);
  }

  nextUserId(userId: number) {
    this.userIdSource.next(userId);
  }
}
