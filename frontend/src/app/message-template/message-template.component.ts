import {Component, Input, OnInit} from '@angular/core';
import {Message} from "../entity/Message";

@Component({
  selector: 'message-template',
  templateUrl: './message-template.component.html',
  styleUrls: ['./message-template.component.css']
})
export class MessageTemplateComponent implements OnInit {

  @Input()
  message: Message;

  checkbox: boolean = false;

  constructor() {
  }

  ngOnInit() {
  }


}
