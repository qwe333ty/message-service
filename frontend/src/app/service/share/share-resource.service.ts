import {Injectable} from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ShareResourceService {

  // Observable string sources
  private authorizedSource = new Subject<boolean>();

  // Observable string streams
  authorized$ = this.authorizedSource.asObservable();

  emitAuthorizedObs(bln: boolean) {
    this.authorizedSource.next(bln);
  }
}
