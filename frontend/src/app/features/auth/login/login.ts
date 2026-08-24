import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loading = signal(false);
  errorMessage = signal('');

  form = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');

    const credentials = {
      username: this.form.value.username!,
      password: this.form.value.password!
    };

    this.authService.login(credentials).subscribe({
      next: response => {
        this.loading.set(false);
        const target = response.role === 'ADMIN' ? '/admin/dashboard' : '/my-tasks';
        this.router.navigate([target]);
      },
      error: err => {
        this.loading.set(false);
        this.errorMessage.set(err.error?.message ?? 'Login failed. Please try again.');
      }
    });
  }
}