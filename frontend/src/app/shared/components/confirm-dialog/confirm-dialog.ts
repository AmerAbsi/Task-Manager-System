import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  imports: [],
  templateUrl: './confirm-dialog.html'
})
export class ConfirmDialog {

  title = input('Are you sure?');
  message = input('This action cannot be undone.');
  confirmLabel = input('Delete');

  confirmed = output<void>();
  cancelled = output<void>();
}