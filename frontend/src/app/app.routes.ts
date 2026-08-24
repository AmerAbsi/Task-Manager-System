import { Routes } from '@angular/router';
import { Login } from './features/auth/login/login';
import { Layout } from './shared/components/layout/layout';
import { authGuard } from './core/guards/auth.guard';
import { adminGuard } from './core/guards/admin.guard';
import { guestGuard } from './core/guards/guest.guard';
import { UserList } from './features/admin/users/user-list/user-list';
import { TaskList } from './features/admin/tasks/task-list/task-list';
import { TaskDetail } from './features/tasks/task-detail/task-detail';
import { MyTasks } from './features/user/my-tasks/my-tasks';
import { Profile } from './features/profile/profile';
import { Dashboard } from './features/admin/dashboard/dashboard';
import { ActivityLogPage } from './features/admin/activity-log/activity-log';
export const routes: Routes = [
  {
    path: 'login',
    component: Login,
    canActivate: [guestGuard]
  },
  {
    path: '',
    component: Layout,
    canActivate: [authGuard],
    children: [
      { path: 'admin/dashboard', component: Dashboard, canActivate: [adminGuard] },
      { path: 'admin/users', component: UserList, canActivate: [adminGuard] },
      { path: 'admin/tasks', component: TaskList, canActivate: [adminGuard] },
      { path: 'admin/activity-log', component: ActivityLogPage, canActivate: [adminGuard] },
      { path: 'my-tasks', component: MyTasks },
      { path: 'profile', component: Profile },
      { path: 'tasks/:id', component: TaskDetail },
      { path: '', redirectTo: 'profile', pathMatch: 'full' },
    ]
  },
  { path: '**', redirectTo: 'login' }
];