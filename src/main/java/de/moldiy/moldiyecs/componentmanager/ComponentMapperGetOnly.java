/**
 * Copyright 2020 Moldiy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package de.moldiy.moldiyecs.componentmanager;

/**
 * This CompoenntMapper allows only get A Compoent so its don't need to be syncronized
 * @author david
 *
 * @param <T>
 */
public interface ComponentMapperGetOnly<T extends Component> {
	public T get(int entity);
}