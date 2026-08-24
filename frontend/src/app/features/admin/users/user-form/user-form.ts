import { Component, inject, input, output, signal, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../../core/services/user.service';
import { User } from '../../../../core/models/user.model';
import { ROLES } from '../../../../core/models/enums';

const PASSWORD_PATTERN =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#]).+$/;

@Component({
  selector: 'app-user-form',
  imports: [ReactiveFormsModule],
  templateUrl: './user-form.html'
})
export class UserForm implements OnInit {

  private fb = inject(FormBuilder);
  private userService = inject(UserService);

  editingUser = input<User | null>(null);

  closed = output<void>();
  saved = output<void>();

  saving = signal(false);
  errorMessage = signal('');

  roles = ROLES;

  form = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.pattern(PASSWORD_PATTERN)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
    fullName: ['', [Validators.required, Validators.maxLength(100)]],
    role: ['USER', Validators.required],
    active: [true, Validators.required]
  });

  get isEditMode(): boolean {
    return this.editingUser() !== null;
  }

  ngOnInit() {
    const user = this.editingUser();

    if (user) {
      this.form.patchValue({
        username: user.username,
        email: user.email,
        fullName: user.fullName,
        role: user.role,
        active: user.active
      });

      this.form.controls.username.disable();
      this.form.controls.password.clearValidators();
      this.form.controls.password.updateValueAndValidity();
    }
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set('');

    const value = this.form.getRawValue();
    const user = this.editingUser();

    const request = user
      ? this.userService.update(user.id, {
          email: value.email!,
          fullName: value.fullName!,
          role: value.role as any,
          active: value.active!
        })
      : this.userService.create({
          username: value.username!,
          password: value.password!,
          email: value.email!,
          fullName: value.fullName!,
          role: value.role as any,
          active: value.active!
        });

    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.saved.emit();
      },
      error: err => {
        this.saving.set(false);
        this.errorMessage.set(err.error?.message ?? 'Could not save user.');
      }
    });
  }

  close() {
    this.closed.emit();
  }
}