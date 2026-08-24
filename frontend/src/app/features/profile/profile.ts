import { Component, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';
import { User } from '../../core/models/user.model';

const PASSWORD_PATTERN =
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&#]).+$/;

@Component({
  selector: 'app-profile',
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './profile.html'
})
export class Profile implements OnInit {

  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private authService = inject(AuthService);

  profile = signal<User | null>(null);
  loading = signal(true);
  saving = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  form = this.fb.group({
    fullName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
    currentPassword: [''],
    password: ['', [Validators.minLength(8), Validators.pattern(PASSWORD_PATTERN)]],
    confirmPassword: ['']
  }, { validators: passwordsMatch });

  ngOnInit() {
    this.userService.getProfile().subscribe({
      next: user => {
        this.profile.set(user);
        this.form.patchValue({
          fullName: user.fullName,
          email: user.email
        });
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not load your profile.');
        this.loading.set(false);
      }
    });
  }

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const value = this.form.value;

    this.userService.updateProfile({
      fullName: value.fullName!,
      email: value.email!,
      currentPassword: value.password ? value.currentPassword! : undefined,
      password: value.password ? value.password : undefined
    }).subscribe({
      next: updated => {
        this.profile.set(updated);
        this.saving.set(false);
        this.successMessage.set('Profile updated.');

        this.form.controls.password.reset('');
        this.form.controls.confirmPassword.reset('');
        this.form.controls.currentPassword.reset('');

         const current = this.authService.currentUser();
        if (current) {
          this.authService.updateStoredUser({
            ...current,
            fullName: updated.fullName
          });
        }
      },
      error: err => {
        this.saving.set(false);
        this.errorMessage.set(err.error?.message ?? 'Could not update profile.');
      }
    });
  }
}

function passwordsMatch(group: AbstractControl) {
  const password = group.get('password')?.value;
  const confirm = group.get('confirmPassword')?.value;

  if (!password) {
    return null;
  }

  return password === confirm ? null : { passwordMismatch: true };
}