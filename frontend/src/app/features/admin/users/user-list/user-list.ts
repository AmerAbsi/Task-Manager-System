import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { UserService } from '../../../../core/services/user.service';
import { User } from '../../../../core/models/user.model';
import { Role, ROLES } from '../../../../core/models/enums';
import { UserForm } from '../user-form/user-form';
import { ConfirmDialog } from '../../../../shared/components/confirm-dialog/confirm-dialog';
import { buildPageNumbers } from '../../../../core/utils/table-utils';
@Component({
  selector: 'app-user-list',
  imports: [FormsModule, DatePipe, UserForm, ConfirmDialog],
  templateUrl: './user-list.html'
})
export class UserList implements OnInit {

  private userService = inject(UserService);


  showForm = signal(false);
  editingUser = signal<User | null>(null);
  deletingUser = signal<User | null>(null);

  users = signal<User[]>([]);
  loading = signal(false);
  errorMessage = signal('');

  totalPages = signal(0);
  totalElements = signal(0);
  currentPage = signal(0);

  searchTerm = '';
  roleFilter: Role | '' = '';
  activeFilter: boolean | '' = '';
  roles = ROLES;

  ngOnInit() {
    this.load();
  }

  load(page = 0) {
    this.loading.set(true);
    this.errorMessage.set('');

    this.userService.search({
      search: this.searchTerm,
      role: this.roleFilter,
      active: this.activeFilter,
      page: page,
      size: 10
    }).subscribe({
      next: result => {
        this.users.set(result.content);
        this.totalPages.set(result.totalPages);
        this.totalElements.set(result.totalElements);
        this.currentPage.set(result.number);
        this.loading.set(false);
      },
      error: () => {
        this.errorMessage.set('Could not load users.');
        this.loading.set(false);
      }
    });
  }

  applyFilters() {
    this.load(0);
  }

  resetFilters() {
    this.searchTerm = '';
    this.roleFilter = '';
    this.activeFilter = '';
    this.load(0);
  }

  goToPage(page: number) {
    if (page >= 0 && page < this.totalPages()) {
      this.load(page);
    }
  }

  pageNumbers(): number[] {
    return buildPageNumbers(this.totalPages());
  }


  openCreate() {
    this.editingUser.set(null);
    this.showForm.set(true);
  }

  openEdit(user: User) {
    this.editingUser.set(user);
    this.showForm.set(true);
  }

  closeForm() {
    this.showForm.set(false);
    this.editingUser.set(null);
  }

  onSaved() {
    this.closeForm();
    this.load(this.currentPage());
  }

  confirmDelete(user: User) {
    this.deletingUser.set(user);
  }

  cancelDelete() {
    this.deletingUser.set(null);
  }

  performDelete() {
    const user = this.deletingUser();
    if (!user) {
      return;
    }

    this.userService.delete(user.id).subscribe({
      next: () => {
        this.deletingUser.set(null);
        this.load(this.currentPage());
      },
      error: () => {
        this.deletingUser.set(null);
        this.errorMessage.set('Could not delete user.');
      }
    });
  }
}