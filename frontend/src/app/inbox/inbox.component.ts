import {Component, Input, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {RequestService} from "../service/request/request.service";
import {ShareResourceService} from "../service/share/share-resource.service";
import {concatMap} from "rxjs/operators";
import {ParamHelper} from "../entity/ParamHelper";
import {Message} from "../entity/Message";
import {Page} from "../entity/Page";
import {Subscription} from "rxjs";
import {MatPaginator, PageEvent} from "@angular/material";

@Component({
  selector: 'app-inbox',
  templateUrl: './inbox.component.html',
  styleUrls: ['./inbox.component.css']
})
export class InboxComponent implements OnInit, OnDestroy {

  @Input() messagePage: Page<Message> = new Page<Message>();

  showProgressBar: boolean = false;

  @ViewChild('paginator') paginator: MatPaginator;

  private subscriptions: Subscription[] = [];

  constructor(private requestService: RequestService,
              private resourceService: ShareResourceService) {
  }

  ngOnInit() {
    this.showProgressBar = true;
    console.log('Im here');
    this.subscriptions.push(this.resourceService.userId$.pipe(
      concatMap(value => {
        console.log('Value: ' + value);
        return this.requestService.getMessagePage(
          this.requestService.userId, 0,
          this.paginator ? this.paginator.pageIndex : 10, ParamHelper.trueFalse, ParamHelper.trueFalse);
      })).subscribe(value => {
      this.messagePage = value;
      this.showProgressBar = false;
    }));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(subscription => subscription.unsubscribe());
  }

  setAnotherPage(event: PageEvent) {
    this.showProgressBar = true;
    this.subscriptions.push(this.resourceService.authorized$.pipe(
      concatMap(value => {
        return this.requestService.getMessagePage(
          this.requestService.userId, event.pageIndex,
          event.pageSize, ParamHelper.trueFalse, ParamHelper.trueFalse);
      })).subscribe(value => {
      this.messagePage = value;
      this.showProgressBar = false;
    }));
  }
}
