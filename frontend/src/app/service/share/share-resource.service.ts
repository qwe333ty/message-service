import {Injectable} from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ShareResourceService {

  // Observable string sources
  private authorizedSource = new Subject<boolean>();
  private userIdSource = new Subject<number>();

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
